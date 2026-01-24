package com.adriano.sharetheimage.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.RemoteMediator.MediatorResult.Error
import androidx.paging.RemoteMediator.MediatorResult.Success
import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.local.entity.SearchQueryCrossRef
import com.adriano.sharetheimage.data.local.entity.SearchQueryRemoteKey
import com.adriano.sharetheimage.data.mapper.toEntity
import com.adriano.sharetheimage.data.remote.UnsplashApi
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto


/**
 * SearchRemoteMediator manages the loading of search results from the Network (Unsplash API) 
 * and storing them into the Local Database (Room). It acts as a single source of truth handler.
 *
 * Paging3 uses this to decide WHEN to fetch more data based on the user's scroll position.
 */
@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediator(
    private val query: String,
    private val unsplashApi: UnsplashApi,
    private val database: DatabaseWrapper
) : RemoteMediator<Int, PhotoEntity>() {

    /**
     * The core method called by the Paging library.
     * [loadType] tells us WHY we are loading (Start, End, or Refresh).
     * [state] contains information about the current list (pages already loaded, anchor position).
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {

        // 1. Calculate which page to fetch from the API
        val page = when (loadType) {
            LoadType.REFRESH -> {
                // REFRESH occurs on initial load or when manually invalidated.
                // We find the item closest to the current scroll position to know where to start.
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                val nextKey = remoteKeys?.nextKey?.minus(1) ?: 1
                nextKey
            }

            LoadType.PREPEND -> {
                // PREPEND occurs when user scrolls to the top of the loaded list.
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) return Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }

            LoadType.APPEND -> {
                // APPEND occurs when user scrolls to the bottom of the loaded list.
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                if (nextKey == null) return Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        try {
            // 2. Perform the Network Request
            val response = unsplashApi.searchPhotos(
                query = query,
                page = page,
                perPage = state.config.pageSize
            )
            val photos = response.results

            // 3. Update the Local Database
            updateCache(photos, loadType, page, state)
            return Success(endOfPaginationReached = photos.isEmpty())
        } catch (exception: Exception) {
            return Error(exception)
        }
    }

    private suspend fun updateCache(
        photos: List<UnsplashPhotoDto>,
        loadType: LoadType,
        page: Int,
        state: PagingState<Int, PhotoEntity>
    ) {

        val endOfPaginationReached = photos.isEmpty()

        database.withTransaction {
            if (loadType == LoadType.REFRESH) {
                database.searchQueryRemoteKeyDao().clearRemoteKeys(query)
                database.searchQueryCrossRefDao().clearCrossRefs(query)
            }

            val prevKey = if (page == 1) null else page - 1
            val nextKey = if (endOfPaginationReached) null else page + 1

            // RemoteKeys tell us what the next/prev pages are for each photo ID.
            val keys = photos.map { photo ->
                SearchQueryRemoteKey(
                    searchQuery = query,
                    id = photo.id,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }

            // CrossRefs maintain the order of photos specifically for this search query.
            val crossRefs = photos.mapIndexed { index, photo ->
                SearchQueryCrossRef(
                    searchQuery = query,
                    photoId = photo.id,
                    orderIndex = (page - 1) * state.config.pageSize + index
                )
            }

            val photoEntities = photos.map { it.toEntity() }

            database.searchQueryRemoteKeyDao().insertAll(keys)
            database.photoDao().insertAll(photoEntities)
            database.searchQueryCrossRefDao().insertAll(crossRefs)
        }
    }

    /**
     * Gets the RemoteKey for the last item in the list currently loaded in memory.
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, PhotoEntity>): SearchQueryRemoteKey? {
        val lastItem = state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
        return lastItem?.let { photo ->
            database.searchQueryRemoteKeyDao().getRemoteKey(query, photo.id)
        }
    }

    /**
     * Gets the RemoteKey for the first item in the list currently loaded in memory.
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, PhotoEntity>): SearchQueryRemoteKey? {
        val firstItem = state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
        return firstItem?.let { photo ->
            database.searchQueryRemoteKeyDao().getRemoteKey(query, photo.id)
        }
    }

    /**
     * Gets the RemoteKey for the item closest to where the user is currently scrolling.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, PhotoEntity>): SearchQueryRemoteKey? {
        val anchorPosition = state.anchorPosition
        return anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { photoId ->
                database.searchQueryRemoteKeyDao().getRemoteKey(query, photoId)
            }
        }
    }
}
