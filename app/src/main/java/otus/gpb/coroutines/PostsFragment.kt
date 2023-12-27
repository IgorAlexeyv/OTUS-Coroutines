package otus.gpb.coroutines

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.divider.MaterialDividerItemDecoration
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import otus.gpb.coroutines.databinding.FragmentPostsBinding
import otus.gpb.coroutines.databinding.VhPostBinding
import otus.gpb.coroutines.network.data.Post

class PostsFragment : Fragment() {

    private var binding: FragmentPostsBinding? = null
    private val adapter = PostsAdapter()

    private inline fun withBinding(block: FragmentPostsBinding.() -> Unit) {
        checkNotNull(binding).block()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPostsBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = withBinding {
        super.onViewCreated(view, savedInstanceState)
        posts.adapter = ConcatAdapter(HeaderAdapter(), adapter)
        posts.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

private class HeaderAdapter : RecyclerView.Adapter<HeaderAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        LayoutInflater.from(parent.context).inflate(R.layout.vh_header, parent, false)
    )

    override fun getItemCount(): Int = 1

    override fun onBindViewHolder(holder: Holder, position: Int) = Unit

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.name)
        private val salary: TextView = itemView.findViewById(R.id.salary)

        init {
            name.text = itemView.context.getString(R.string.header_title)
            salary.text = itemView.context.getString(R.string.header_date)
        }
    }
}

private class PostsAdapter : ListAdapter<Post, PostsAdapter.Holder>(PostDiffCallback) {
    class Holder(private val binding: VhPostBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(post: Post) = with(binding) {
            title.text = post.title
            date.text = post.created.toLocalDateTime(TimeZone.currentSystemDefault()).date.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder = Holder(
        VhPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem == newItem
    }
}