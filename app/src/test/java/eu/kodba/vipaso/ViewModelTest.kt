package eu.kodba.vipaso

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import eu.kodba.vipaso.models.Repository
import eu.kodba.vipaso.models.User
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(
    MockitoJUnitRunner::class)
class ViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @ExperimentalCoroutinesApi
    @Test
    fun viewModelValidator_SetQueryCheck() {
        val myViewModel = MyViewModel()
        myViewModel.setQuery("test")
        assertTrue(myViewModel.query.value == "test")
    }

    @ExperimentalCoroutinesApi
    @Test
    fun viewModelValidator_SetUserCheck() = runTest {
        val myViewModel = MyViewModel()
        myViewModel.setUser(User(login = "ingo", id = 1, avatar_url = "http://example.com/image.jpg"))
        assertEquals(myViewModel.repositories.value, listOf<Repository>())
    }

}