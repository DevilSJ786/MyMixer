package com.media.mixer.data.repository

import com.media.mixer.data.dao.UserDao
import com.media.mixer.data.entities.Playlist
import com.media.mixer.data.entities.Song
import com.media.mixer.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow

class LocalRepositoryImp(private val userDao: UserDao):LocalRepository {
    override suspend fun insertPlaylist(playlist: Playlist) {
       userDao.insert(playlist)
    }

    override fun getPlaylist(isVideo: Boolean): Flow<List<Playlist>> {
       return userDao.getPlaylist(isVideo)
    }

    override suspend fun getSinglePlaylist(id: Long): Playlist? {
        return userDao.getSinglePlaylist(id)
    }

    override suspend fun updatePlaylistCount(id: Long, count: Int) {
        userDao.updatePlaylistCount(id, count)
    }

    override suspend fun insertSong(song: Song) {
      userDao.insertSong(song)
    }

    override fun getPlaylistSong(): Flow<List<Song>> {
       return userDao.getPlaylistSong()
    }

    override suspend fun updateSongFav(id: Int, fav: Boolean) {
        userDao.updateSongFav(id, fav)
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        userDao.deletePlaylist(playlist)
    }
}