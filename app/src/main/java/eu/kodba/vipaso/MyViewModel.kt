package eu.kodba.vipaso

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import eu.kodba.vipaso.models.Repository
import eu.kodba.vipaso.models.User
import eu.kodba.vipaso.models.UserList
import eu.kodba.vipaso.network.APIClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope.coroutineContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.CoroutineContext

open class MyViewModel : ViewModel() {

    var apiInterface = APIClient().apiInterface
    var debounceDelay = 1000L

    private val _users = MutableLiveData<List<User>>()
    val users: LiveData<List<User>> = _users

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _query = MutableLiveData<String>("")
    val query: LiveData<String> = _query

    private val _chosenUser = MutableLiveData<User?>(null)
    val chosenUser: LiveData<User?> = _chosenUser

    private val _loadingUsers = MutableLiveData<Boolean>(false)
    val loadingUsers: LiveData<Boolean> = _loadingUsers

    private val _loadingRepositories = MutableLiveData<Boolean>(false)
    val loadingRepositories: LiveData<Boolean> = _loadingRepositories

    private val _repositories = MutableLiveData<List<Repository>>(listOf())
    val repositories: LiveData<List<Repository>> = _repositories

    fun <T> debounce(delayMs: Long = debounceDelay,
                     coroutineContext: CoroutineContext,
                     f: (T) -> Unit): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            if (debounceJob?.isCompleted != false) {
                debounceJob = CoroutineScope(coroutineContext).launch {
                    delay(delayMs)
                    withContext(Dispatchers.Main){
                        f(param)
                    }
                }
            }
        }
    }

    val handleClickEventsDebounced = debounce<Unit>(1000, coroutineContext) {
        _query.value?.let { it1 ->
            fetchUsers(it1)
        }
    }

    init{
        viewModelScope.launch{
            _query.asFlow().collect{
                if(it.length >= 2) {
                    _loadingUsers.value = true
                    Log.d("ingo", "query changed")
                    handleClickEventsDebounced(Unit)
                }
            }
        }
    }

    fun getUserRepositories(username: String){
        _loadingRepositories.value = true
        viewModelScope.launch {
            try {
                val call = apiInterface.doGetUserStarredRepositories(username)
                call.enqueue(object : Callback<List<Repository>> {
                    override fun onResponse(call: Call<List<Repository>>, response: Response<List<Repository>>) {
                        Log.d("ingo", response.code().toString())
                        Log.d("ingo", response.message().toString())
                        _loadingRepositories.value = false
                        if(!response.isSuccessful){
                            Log.d("ingo", "errors")
                            _error.value = response.code().toString()
                        } else {
                            _error.value = null
                            Log.v("ingo", response.body().toString())
                            _repositories.value = response.body()
                        }
                    }

                    override fun onFailure(call: Call<List<Repository>>, t: Throwable) {
                        call.cancel()
                        _loadingRepositories.value = false
                        _error.value = "Offline"
                    }
                })
            } catch (e: Exception) {
                // Handle the error
                _error.value = "Exception"
                Log.e("ingo", e.toString())
            }
        }
    }

    fun fetchUsers(query: String) {
        _loadingUsers.value = true
        viewModelScope.launch {
            try {
                val call = apiInterface.doGetUserList("$query in:name", 0)
                call.enqueue(object : Callback<UserList> {
                    override fun onResponse(call: Call<UserList>, response: Response<UserList>) {
                        Log.d("ingo", response.code().toString())
                        Log.d("ingo", response.message().toString())
                        _loadingUsers.value = false
                        if(!response.isSuccessful){
                            Log.d("ingo", "errors")
                            _error.value = response.code().toString()
                        } else {
                            _error.value = null
                            Log.v("ingo", response.body().toString())
                            _users.value = response.body()?.items!!
                        }
                    }

                    override fun onFailure(call: Call<UserList>, t: Throwable) {
                        call.cancel()
                        _loadingUsers.value = false
                        _error.value = "Offline"
                    }
                })
            } catch (e: Exception) {
                // Handle the error
                _error.value = "Exception"
                Log.e("ingo", e.toString())
            }
        }
    }

    fun setUser(userToSet: User){
        _chosenUser.value = userToSet
        _repositories.value = listOf()
        getUserRepositories(userToSet.login)
    }

    fun setQuery(query: String){
        _query.value = query
    }

    fun clearError(){
        _error.value = null
    }
}