package io.github.feelfree.kotlinmvpdaggerexample.ui.adapter.viewholders

import android.support.v7.widget.RecyclerView
import android.view.View
import io.github.feelfree.kotlinmvpdaggerexample.api.models.dataclass.GithubRepository
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.github_repository_list_item.*

// Use layoutContainer to cache views. It improves scroll performance
class GithubRepositoryViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    fun bindView(repository : GithubRepository) {
        repositoryDescriptionTextView.text = repository.description
        repositoryTitleTextView.text = repository.name
    }
}