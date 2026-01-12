package com.adriano.sharetheimage.ui.home

import app.cash.turbine.test
import com.adriano.sharetheimage.domain.model.Photo
import com.adriano.sharetheimage.domain.usecase.GetPhotosUseCase
import com.adriano.sharetheimage.domain.usecase.LoadMorePhotosUseCase
import com.adriano.sharetheimage.domain.usecase.SearchPhotosUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val searchPhotosUseCase: SearchPhotosUseCase = mockk(relaxed = true)
    private val getPhotosUseCase: GetPhotosUseCase = mockk()
    private val loadMorePhotosUseCase: LoadMorePhotosUseCase = mockk(relaxed = true)
    
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Mock getPhotos flow
        every { getPhotosUseCase() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `init triggers search Nature`() = runTest {
        viewModel = HomeViewModel(searchPhotosUseCase, getPhotosUseCase, loadMorePhotosUseCase)
        testDispatcher.scheduler.advanceUntilIdle()
        
        coVerify { searchPhotosUseCase("Nature") }
    }

    @Test
    fun `search updates searchQuery and triggers useCase`() = runTest {
        viewModel = HomeViewModel(searchPhotosUseCase, getPhotosUseCase, loadMorePhotosUseCase)
        testDispatcher.scheduler.advanceUntilIdle() // Process init
        
        viewModel.search("Cars")
        testDispatcher.scheduler.advanceUntilIdle()
        
        assertEquals("Cars", viewModel.uiState.value.searchQuery)
        coVerify { searchPhotosUseCase("Cars") }
    }
}
