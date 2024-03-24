package com.app.news.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.app.news.R
import com.app.news.databinding.SearchItemsBinding
import com.app.news.domain.model.ArticlesItem
import com.app.news.utils.Util
import javax.inject.Inject

class NewsAdapter @Inject constructor() :
    PagingDataAdapter<ArticlesItem, NewsAdapter.NewsViewHolder>(differCallback) {

    var listener: OnClickListener? = null

    interface OnClickListener {
        fun onCardClick(url: String)
        fun onSaveButton(newsItem: ArticlesItem)
        fun onShareButton(newsItem: ArticlesItem)
    }

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<ArticlesItem>() {
            override fun areItemsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: ArticlesItem, newItem: ArticlesItem): Boolean {
                return oldItem == newItem
            }

        }
    }

    override fun onBindViewHolder(holder: NewsAdapter.NewsViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NewsAdapter.NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SearchItemsBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)
    }

    inner class NewsViewHolder(private val binding: SearchItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    newsItem?.url?.let { it1 -> listener?.onCardClick(it1) }
                }
                ivSaveArticle.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        listener?.onSaveButton(newsItem)
                        ivSaveArticle.setImageResource(R.drawable.ic_saved)
                    }
                }

                ivShareArticle.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        listener?.onShareButton(newsItem)

                    }
                }
            }

        }

        fun bind(currentItem: ArticlesItem) {
            binding.apply {

                tvSource.text = currentItem.source?.name ?: "unknown"
                tvTitle.text = currentItem.title
                tvPublishedAt.text = currentItem.publishedAt?.let { Util.formatDate(it) }

                if (currentItem.isSaved) {
                    ivSaveArticle.setImageResource(R.drawable.ic_saved)
                } else {
                    ivSaveArticle.setImageResource(R.drawable.ic_save)
                }
                val imgUrl = currentItem.urlToImage

                imgUrl?.let {
                    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
                    ivNewsImage.load(imgUri) {
                        placeholder(R.drawable.placeholder)
                        transformations(RoundedCornersTransformation(10f))
                    }
                }
            }


        }


    }
}