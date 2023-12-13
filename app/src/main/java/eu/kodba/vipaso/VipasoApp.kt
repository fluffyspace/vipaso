package eu.kodba.vipaso

import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import eu.kodba.vipaso.ui.Conversation
import eu.kodba.vipaso.ui.DetailsScreen

enum class VipasoScreen(@StringRes val title: Int) {
    Start(title = R.string.start),
    Details(title = R.string.details),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipasoAppBar(
    currentScreen: VipasoScreen,
    canNavigateBack: Boolean = false,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VipasoApp(
    viewModel: MyViewModel = viewModel(),
    navController: NavHostController = rememberNavController()
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = VipasoScreen.valueOf(
        backStackEntry?.destination?.route ?: VipasoScreen.Start.name
    )

    Scaffold(
        topBar = {
            VipasoAppBar(
                currentScreen = currentScreen,
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateUp = {
                    navController.navigateUp()
                    viewModel.clearError()
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = VipasoScreen.Start.name,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = VipasoScreen.Start.name) {
                Conversation(
                    myViewModel = viewModel,
                    onUserClick = {
                        viewModel.setUser(it)
                        Log.e("ingo", it.toString())
                        navController.navigate(VipasoScreen.Details.name)
                    },
                )
            }
            composable(route = VipasoScreen.Details.name) {
                //val context = LocalContext.current
                DetailsScreen(myViewModel = viewModel)
            }
        }
    }
}