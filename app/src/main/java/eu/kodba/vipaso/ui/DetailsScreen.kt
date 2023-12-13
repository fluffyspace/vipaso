package eu.kodba.vipaso.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import eu.kodba.vipaso.MyViewModel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun DetailsScreen(
    myViewModel: MyViewModel = viewModel()
) {
    val user by myViewModel.chosenUser.observeAsState(null)
    val repositories by myViewModel.repositories.observeAsState(listOf())
    val loadingRepositories by myViewModel.loadingRepositories.observeAsState(false)
    val error by myViewModel.error.observeAsState(null)
    if(user != null) {
        Column(modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())){
            Text(modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(), text= user!!.login, fontSize = 18.sp, textAlign = TextAlign.Center)
            GlideImage(
                model = user!!.avatar_url,
                contentDescription = "Profile picture",
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            )
            Text(
                text="Starred repositories:",
                style = MaterialTheme.typography.titleLarge
            )
            if(loadingRepositories){
                LoadingBox()
            } else if(error != null) {
                ErrorBox(error = error)
            } else {
                if(repositories.isNotEmpty()) {
                    repositories.forEach { repository ->
                        Text(
                            text = repository.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        if(repository.description != null){
                            Text(text = repository.description!!)
                        } else {
                            Text(text = "No description.")
                        }
                        Divider()
                    }
                } else {
                    Text(
                        text = "No starred repositories.",
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}