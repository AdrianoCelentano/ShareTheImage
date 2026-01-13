package com.adriano.sharetheimage.data.remote

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import coil.network.HttpException
import com.adriano.sharetheimage.data.remote.dto.UnsplashPhotoDto
import kotlinx.io.IOException
import javax.inject.Inject

class UnsplashNetworkPagingSource(
    val backend: UnsplashApi,
    val query: String
) : PagingSource<Int, UnsplashPhotoDto>() {
    override suspend fun load(
        params: LoadParams<Int>
    ): LoadResult<Int, UnsplashPhotoDto> {
        try {
            val nextPageNumber = params.key ?: 1
            val response = backend.searchPhotos(query, nextPageNumber)
            return LoadResult.Page(
                data = response.results,
                prevKey = null, // Only paging forward.
                nextKey = nextPageNumber + 1
            )
        } catch (e: IOException) {
            Log.d("paging", e.toString())
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Log.d("paging", e.toString())
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, UnsplashPhotoDto>): Int? {
        // return the starting key to use if data gets refreshed
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }
}

class PagerFactory @Inject constructor(
    val backend: UnsplashApi,
) {
    fun create(query: String): Pager<Int, UnsplashPhotoDto> {
        return Pager(
            PagingConfig(pageSize = 20)
        ) { UnsplashNetworkPagingSource(backend, query) }
    }
}