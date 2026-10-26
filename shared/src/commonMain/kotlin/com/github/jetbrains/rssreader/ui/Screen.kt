package com.github.jetbrains.rssreader.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.github.jetbrains.rssreader.Res
import com.github.jetbrains.rssreader.app.FeedAction
import com.github.jetbrains.rssreader.app.FeedStore
import com.github.jetbrains.rssreader.app_name
import com.github.jetbrains.rssreader.back_button
import com.github.jetbrains.rssreader.core.RssReader
import com.github.jetbrains.rssreader.domain.SavedPost
import com.github.jetbrains.rssreader.domain.toSavedPost
import com.github.jetbrains.rssreader.feed_list
import com.github.jetbrains.rssreader.no_saved_posts
import com.github.jetbrains.rssreader.open_in_browser
import com.github.jetbrains.rssreader.post
import com.github.jetbrains.rssreader.remove
import com.github.jetbrains.rssreader.save
import com.github.jetbrains.rssreader.saved
import com.github.jetbrains.rssreader.saved_posts
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

enum class Screen(val title: StringResource) {
    Main(Res.string.app_name),
    FeedList(Res.string.feed_list),
    Post(Res.string.post),
    Saved(Res.string.saved_posts);
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RssFeedAppBar(
    currentScreen: Screen,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { Text(stringResource(currentScreen.title)) },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        modifier = modifier,
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(Res.string.back_button)
                    )
                }
            }
        }
    )
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MainScreen(
    onEditClick: () -> Unit,
    onSavedClick: () -> Unit,
    onPostClick: (SavedPost) -> Unit,
    modifier: Modifier = Modifier
) {
    val store: FeedStore = koinInject<FeedStore>()
    val state by store.observeState().collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        store.dispatch(FeedAction.Refresh(false))
    }
    PullToRefreshBox(
        isRefreshing = state.progress,
        onRefresh = { store.dispatch(FeedAction.Refresh(true)) },
        modifier = modifier,
        content = {
            MainFeed(
                store = store,
                onPostClick = { post -> onPostClick(post.toSavedPost()) },
                onEditClick = onEditClick,
                onSavedClick = onSavedClick
            )
        })
}

@Composable
fun FeedListScreen() {
    val store: FeedStore = koinInject<FeedStore>()
    FeedList(store = store)
}

@Composable
fun PostDetailScreen(post: SavedPost) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        post.title?.let {
            Text(text = it, style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.size(16.dp))
        }
        post.imageUrl?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.size(16.dp))
        }
        post.description?.let {
            Text(text = it, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.size(16.dp))
        }
        post.pubDate?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun PostBottomBar(post: SavedPost) {
    val reader = koinInject<RssReader>()
    val savedPosts by reader.savedPosts.collectAsStateWithLifecycle()
    val isSaved = savedPosts.any { it.link == post.link }
    val uriHandler = LocalUriHandler.current
    BottomAppBar {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { reader.toggleSaved(post) }) {
                Text(stringResource(if (isSaved) Res.string.saved else Res.string.save))
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = { uriHandler.openUri(post.link) }) {
                Text(stringResource(Res.string.open_in_browser))
            }
        }
    }
}

@Composable
fun SavedPostsScreen(onPostClick: (SavedPost) -> Unit) {
    val reader = koinInject<RssReader>()
    val savedPosts by reader.savedPosts.collectAsStateWithLifecycle()
    if (savedPosts.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.size(32.dp))
            Text(stringResource(Res.string.no_saved_posts))
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(savedPosts, key = { it.link }) { post ->
                Card(modifier = Modifier.padding(bottom = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPostClick(post) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = post.title ?: post.link,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.weight(1f)
                        )
                        TextButton(onClick = { reader.toggleSaved(post) }) {
                            Text(stringResource(Res.string.remove))
                        }
                    }
                }
            }
        }
    }
}
