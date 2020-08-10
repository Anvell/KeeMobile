package io.github.anvell.keemobile.core.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyRecyclerView

open class BaseEpoxyAdapter(
    @LayoutRes private val layoutId: Int,
    @IdRes private val epoxyId: Int,
    private val setupBlock: (EpoxyRecyclerView.() -> Unit)? = null
) : RecyclerView.Adapter<BaseEpoxyViewHolder>() {

    private var models: List<EpoxyController.() -> Unit> = listOf()

    open fun updateModels(models: List<EpoxyController.() -> Unit>) {
        this.models = models
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseEpoxyViewHolder {
        val viewHolder = BaseEpoxyViewHolder(
            LayoutInflater.from(parent.context).inflate(layoutId, parent, false),
            epoxyId
        )
        setupBlock?.also { it(viewHolder.getRootRecycler()) }

        return viewHolder
    }

    override fun getItemCount(): Int = models.size

    override fun onBindViewHolder(holder: BaseEpoxyViewHolder, position: Int) = holder.run {
        if (models.size > position) {
            holder.getRootRecycler().withModels(models[position])
        }
    }
}

open class BaseEpoxyViewHolder(root: View, @IdRes epoxyId: Int) : RecyclerView.ViewHolder(root) {

    private val recyclerView: EpoxyRecyclerView = root.findViewById(epoxyId)

    open fun getRootRecycler() = recyclerView
}
