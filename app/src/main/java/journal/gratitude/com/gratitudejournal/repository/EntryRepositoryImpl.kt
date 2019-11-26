package journal.gratitude.com.gratitudejournal.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import journal.gratitude.com.gratitudejournal.model.Entry
import journal.gratitude.com.gratitudejournal.room.EntryDao
import journal.gratitude.com.gratitudejournal.util.OpenForTesting
import org.threeten.bp.LocalDate
import javax.inject.Inject


class EntryRepositoryImpl @Inject constructor(private val entryDao: EntryDao): EntryRepository {

    companion object {
        private const val PAGE_SIZE = 20
    }

    override fun getEntry(date: LocalDate): LiveData<Entry> {
        return entryDao.getEntry(date)
    }

    override fun getAllEntries(): LiveData<List<Entry>> {
        return entryDao.getEntries()
    }

    override fun getWrittenDates(): LiveData<List<LocalDate>> {
        return entryDao.getWrittenDates()
    }

    @WorkerThread
    override suspend fun addEntry(entry: Entry) {
        if (entry.entryContent.isEmpty()) {
            entryDao.delete(entry)
        } else {
            entryDao.insertEntry(entry)
        }
    }

    @WorkerThread
    override suspend fun addEntries(entries: List<Entry>) {
        entryDao.insertEntries(entries)
    }

    override fun searchEntries(query: String): LiveData<PagedList<Entry>> {
        val wildcardQuery = String.format("*%s*", query)

        val pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(true)
            .setInitialLoadSizeHint(PAGE_SIZE)
            .setPageSize(PAGE_SIZE)
            .build()
        return LivePagedListBuilder(entryDao.searchAllEntries(wildcardQuery), pagedListConfig).build()
    }

}