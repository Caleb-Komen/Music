package com.techdroidcentre.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.techdroidcentre.data.mapper.toModel
import com.techdroidcentre.data.repository.AlbumsRepository
import com.techdroidcentre.data.repository.DefaultAlbumsRepository
import com.techdroidcentre.data.repository.TopAlbumsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val topAlbumsRepository: TopAlbumsRepository,
    private val albumsRepository: AlbumsRepository
): ViewModel(){
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchTopAlbums()
    }

    private fun fetchTopAlbums() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(loading = true)
            }
        topAlbumsRepository.getTopAlbums()
            .collect { topAlbums ->
                val albums = topAlbums.map {
                    (albumsRepository as DefaultAlbumsRepository).albums.first {
                            album -> it.albumId == album.id
                    }.toModel()
                }.take(10)
                _uiState.update {
                    it.copy(topAlbums = albums, error = "", loading = false)
                }
            }
        }
    }
}