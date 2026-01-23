package com.adriano.sharetheimage.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator.MediatorResult
import androidx.paging.RemoteMediator.MediatorResult.Success
import com.adriano.sharetheimage.data.fakes.FakeDatabaseWrapper
import com.adriano.sharetheimage.data.local.DatabaseWrapper
import com.adriano.sharetheimage.data.local.entity.PhotoEntity
import com.adriano.sharetheimage.data.local.entity.SearchQueryRemoteKey
import com.adriano.sharetheimage.data.remote.mock.MockConfig.Mode
import com.adriano.sharetheimage.data.remote.mock.fakeUnsplashApi
import com.adriano.sharetheimage.domain.model.RateLimitException
import com.adriano.sharetheimage.photoEntitiy
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class SearchRemoteMediatorTest {

    @Test
    fun `refresh load success and returns endOfPagination false`() = runTest {
        // Given
        val database = FakeDatabaseWrapper()
        val mediator = searchRemoteMediator(databaseWrapper = database)
        val pagingState = pagingState()

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).endOfPaginationReached).isFalse
        val photos = database.fakePhotoDao.getAllPhotos()
        assertTrue(photos.isNotEmpty())
    }

    @Test
    fun `refresh load success and returns endOfPagination true`() = runTest {
        // Given
        val database = FakeDatabaseWrapper()
        val mediator = searchRemoteMediator(databaseWrapper = database, mode = Mode.EmptyList)
        val pagingState = pagingState()

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).endOfPaginationReached).isTrue
        val photos = database.fakePhotoDao.getAllPhotos()
        assertThat(photos).isEmpty()
    }

    @Test
    fun `refresh load returns error when api fails`() = runTest {
        // Given
        val database = FakeDatabaseWrapper()
        val mediator = searchRemoteMediator(databaseWrapper = database, mode = Mode.ErrorGeneral)
        val pagingState = pagingState()

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertTrue(result is MediatorResult.Error)
        val photos = database.fakePhotoDao.getAllPhotos()
        assertThat(photos).isEmpty()
    }

    @Test
    fun `refresh load returns rate limit error when api rate limit`() = runTest {
        // Given
        val database = FakeDatabaseWrapper()
        val mediator = searchRemoteMediator(databaseWrapper = database, mode = Mode.ErrorRateLimit)
        val pagingState = pagingState()

        // When
        val result = mediator.load(LoadType.REFRESH, pagingState)

        // Then
        assertThat((result as MediatorResult.Error).throwable).isInstanceOf(RateLimitException::class.java)
        val photos = database.fakePhotoDao.getAllPhotos()
        assertThat(photos).isEmpty()
    }

    @Test
    fun `append load success`() = runTest {
        // Given
        val database = FakeDatabaseWrapper()
        val mediator = searchRemoteMediator(databaseWrapper = database, mode = Mode.Success)

        // Setup initial data in DB for the "last item"
        val photoId = "photo1"
        val remoteKey = SearchQueryRemoteKey(
            searchQuery = "Cats",
            id = photoId,
            prevKey = 1,
            nextKey = 2
        )
        database.fakeRemoteKeyDao.insertAll(listOf(remoteKey))

        // Create a PagingState that has this item as the last item
        val photo = photoEntitiy()
        val page = PagingSource.LoadResult.Page(
            data = listOf(photo),
            prevKey = 1,
            nextKey = 2
        )
        val pagingState = PagingState(
            pages = listOf(page),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )
        // initial load of first items
        mediator.load(LoadType.REFRESH, pagingState())

        // When
        val result = mediator.load(LoadType.APPEND, pagingState)

        // Then
        assertThat(result).isInstanceOf(Success::class.java)
        assertThat((result as Success).endOfPaginationReached).isFalse

        // Verify that database has been populated with more photos
        val photos = database.fakePhotoDao.getAllPhotos()
        assertThat(photos).hasSize(4)
        assertThat(photos.map { it.id }).containsExactly("1", "2", "3", "4")
    }


}

private fun pagingState(): PagingState<Int, PhotoEntity> = PagingState<Int, PhotoEntity>(
    pages = listOf(),
    anchorPosition = null,
    config = PagingConfig(pageSize = 10),
    leadingPlaceholderCount = 0
)

fun searchRemoteMediator(
    query: String = "Cats",
    databaseWrapper: DatabaseWrapper = FakeDatabaseWrapper(),
    mode: Mode = Mode.Success
): SearchRemoteMediator {
    val api = fakeUnsplashApi(mode)
    return SearchRemoteMediator(query, api, databaseWrapper)
}
