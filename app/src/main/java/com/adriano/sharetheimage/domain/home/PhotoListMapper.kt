package com.adriano.sharetheimage.domain.home

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.LoadState.Loading
import androidx.paging.compose.LazyPagingItems
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.model.RateLimitException

val CombinedLoadStates.isLoading: Boolean get() = refresh is Loading || append is Loading

val CombinedLoadStates.error: PhotoListUIStateError?
    get() = when {
        hasError || (append as? LoadState.Error)?.error is RateLimitException -> LimitReachedError
        hasError || (refresh as? LoadState.Error)?.error is RateLimitException -> LimitReachedError
        hasError -> GeneralError
        else -> null
    }

val LazyPagingItems<Photo>.isEmpty: Boolean
    get() = (loadState.refresh.endOfPaginationReached || loadState.append.endOfPaginationReached)
            && itemCount == 0

sealed class PhotoListUIStateError
object GeneralError : PhotoListUIStateError()
object LimitReachedError : PhotoListUIStateError()