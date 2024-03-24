package com.app.news.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.app.news.R
import com.app.news.databinding.ArticleItemsBinding
import com.app.news.db.NewsLocalModel
import com.app.news.utils.Util

class BookMarksAdapter(private val listener: ButtonClickListener) :
    ListAdapter<NewsLocalModel, BookMarksAdapter.ViewHolder>(NewsLocalModelDiffCallback()) {

    interface ButtonClickListener {
        fun onCardClicked(url: NewsLocalModel)
        fun onRemoveButton(newsItem: NewsLocalModel)
    }

    inner class ViewHolder(private val binding: ArticleItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = absoluteAdapterPosition
                val newsItem = getItem(position)
                newsItem?.url?.let { it1 -> listener.onCardClicked(newsItem) }
            }
            binding.ivSave.setOnClickListener {
                val position = absoluteAdapterPosition
                val newsItem = getItem(position)
                listener.onRemoveButton(newsItem)
            }
        }

        fun bind(newsLocalModel: NewsLocalModel) {
            binding.apply {
                ivShare.isVisible = false
                ivSave.setImageResource(R.drawable.ic_remove)
                tvArtiCleTitle.text = newsLocalModel.title
                tvSource.text = newsLocalModel.source
                tvPublished.text = Util.formatDate(newsLocalModel.publishedAt)

                val imgUrl = newsLocalModel.urlToImage
                imgUrl.let {
                    val imgUri = it.toUri().buildUpon().scheme("https").build()
                    ivArticle.load(imgUri) {
                        transformations(RoundedCornersTransformation(25f))
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ArticleItemsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class NewsLocalModelDiffCallback : DiffUtil.ItemCallback<NewsLocalModel>() {
    override fun areItemsTheSame(oldItem: NewsLocalModel, newItem: NewsLocalModel): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: NewsLocalModel, newItem: NewsLocalModel): Boolean {
        return oldItem == newItem
    }
}
