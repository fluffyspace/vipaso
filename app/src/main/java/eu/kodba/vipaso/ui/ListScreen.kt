package eu.kodba.vipaso.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import eu.kodba.vipaso.MyViewModel
import eu.kodba.vipaso.R
import eu.kodba.vipaso.models.User
import eu.kodba.vipaso.ui.theme.VipasoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Conversation(onUserClick: (User) -> Unit,
                 myViewModel: MyViewModel = viewModel()
) {
    val users by myViewModel.users.observeAsState(emptyList())
    val loading by myViewModel.loadingUsers.observeAsState(false)
    val error by myViewModel.error.observeAsState(null)
    val query by myViewModel.query.observeAsState("")

    Column {
        TextField(modifier = Modifier.fillMaxWidth(), value = query, onValueChange = {
            myViewModel.setQuery(it)
        }, label = { Text("Username") }
        )
        if(loading) {
            LoadingBox()
        } else {
            if(error == null) {
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(users) { user ->
                        UserCard(user, onUserClick = { onUserClick(it) })
                    }
                }
            } else {
                ErrorBox(error = error)
            }
        }
    }
}

@Composable
fun ErrorBox(error: String?){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = if(error == "Offline") R.drawable.baseline_wifi_off_24 else R.drawable.baseline_error_24),
                contentDescription = "Network error",
                contentScale = ContentScale.Crop,
                modifier = Modifier.width(100.dp)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                text = if(error == "Offline") "Network error" else "Error code: ${error.toString()}",
                textAlign = TextAlign.Center,
                fontSize = 18.sp
            )
        }
    }
}

@Composable
fun LoadingBox(){
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.width(48.dp),
                color = MaterialTheme.colorScheme.secondary,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Loading...", textAlign = TextAlign.Center)
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    VipasoTheme {
        Conversation(
            onUserClick = {},
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun UserCard(user: User, onUserClick: (User) -> Unit) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(all = 8.dp)
        .clickable { onUserClick(user) },
    verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            model = user.avatar_url,
            contentDescription = "Profile picture",
            modifier = Modifier
                .padding(4.dp)
                .height(50.dp)
                .width(50.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column() {
            Text(
                text = user.login,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleLarge,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}
