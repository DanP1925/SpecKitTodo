package com.example.taskprioritylist.di

import com.example.taskprioritylist.data.di.DatabaseModule
import com.example.taskprioritylist.domain.repository.TaskRepository
import com.example.taskprioritylist.fake.FakeTaskRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DatabaseModule::class],
)
abstract class TestDatabaseModule {
    @Binds
    @Singleton
    abstract fun bindTaskRepository(impl: FakeTaskRepository): TaskRepository
}
