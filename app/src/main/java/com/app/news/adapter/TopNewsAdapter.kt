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
import com.app.news.databinding.ArticleItemsBinding
import com.app.news.domain.model.ArticlesItem
import com.app.news.utils.Util
import javax.inject.Inject

class TopNewsAdapter @Inject constructor() :
    PagingDataAdapter<ArticlesItem, TopNewsAdapter.NewsViewHolder>(differCallback) {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ArticleItemsBinding.inflate(inflater, parent, false)
        return NewsViewHolder(binding)

    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = getItem(position)
        if (currentItem != null) {
            holder.bind(currentItem)
        }
    }

    inner class NewsViewHolder(private val binding: ArticleItemsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {

            binding.apply {
                root.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        newsItem.url?.let { it1 ->
                            listener?.onCardClick(it1)
//                            openNewsArticleInChrome(it1, itemView.context)
                        }
                    }
                }

                ivSave.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        listener?.onSaveButton(newsItem)
                        ivSave.setImageResource(R.drawable.ic_saved)
                    }
                }

                ivShare.setOnClickListener {
                    val position = absoluteAdapterPosition
                    val newsItem = getItem(position)
                    if (newsItem != null) {
                        listener?.onShareButton(newsItem)

                    }

                }


            }
        }

        fun bind(article: ArticlesItem) {
            binding.apply {

                val imgUrl = article.urlToImage

                imgUrl?.let {
                    val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()

                    ivArticle
                        .load(imgUri) {
                            placeholder(R.drawable.placeholder)
                            transformations(RoundedCornersTransformation(20f))
                        }
                }

                tvArtiCleTitle.text = article.title
                tvSource.text = article.source?.name ?: "Unknown"

                tvPublished.text = article.publishedAt?.let {
                    Util.formatDate(it)
                }
                if (article.isSaved) {
                    ivSave.setImageResource(R.drawable.ic_saved)
                } else {
                    ivSave.setImageResource(R.drawable.ic_save)
                }

//                saveArticleImg.isVisible = true


            }
        }
    }


}