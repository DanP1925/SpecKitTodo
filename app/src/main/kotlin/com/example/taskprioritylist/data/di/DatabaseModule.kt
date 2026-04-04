package com.example.taskprioritylist.data.di

import android.content.Context
import androidx.room.Room
import com.example.taskprioritylist.data.local.TaskDao
import com.example.taskprioritylist.data.local.TaskDatabase
import com.example.taskprioritylist.data.repository.TaskRepositoryImpl
import com.example.taskprioritylist.domain.repository.TaskRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl): TaskRepository

    companion object {
        @Provides
        @Singleton
        fun provideTaskDatabase(
            @ApplicationContext context: Context,
        ): TaskDatabase =
            Room.databaseBuilder(
                context,
                TaskDatabase::class.java,
                "task_priority_list.db",
            ).build()

        @Provides
        @Singleton
        fun provideTaskDao(database: TaskDatabase): TaskDao = database.taskDao()
    }
}
