package com.prt2121.sectionlist

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import java.util.*

/**
 * Created by pt2121 on 2/6/16.
 */
class SectionRecyclerViewAdapter<T : RecyclerView.ViewHolder>(val context: Context,
                                   val sectionResourceId: Int,
                                   val textResourceId: Int,
                                   val baseAdapter: RecyclerView.Adapter<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

  val sections = SparseArray<Section>()
  private var valid = true

  init {
    baseAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
      override fun onChanged() {
        valid = baseAdapter.itemCount > 0
        notifyDataSetChanged()
      }

      override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        valid = baseAdapter.itemCount > 0
        notifyItemRangeChanged(positionStart, itemCount)
      }

      override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        valid = baseAdapter.itemCount > 0
        notifyItemRangeInserted(positionStart, itemCount)
      }

      override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        valid = baseAdapter.itemCount > 0
        notifyItemRangeRemoved(positionStart, itemCount)
      }
    })
  }

  override fun getItemId(position: Int): Long {
    return if (isSectionHeaderPosition(position)) Long.MAX_VALUE - sections.indexOfKey(position)
    else baseAdapter.getItemId(sectionedPositionToPosition(position))
  }

  override fun getItemCount(): Int {
    return if (valid) baseAdapter.itemCount + sections.size() else 0
  }

  override fun onCreateViewHolder(p0: ViewGroup?, p1: Int): RecyclerView.ViewHolder? {
    return if (p1 == SECTION_TYPE) {
      val view = LayoutInflater.from(context).inflate(sectionResourceId, p0, false)
      SectionViewHolder(view, textResourceId)
    } else {
      baseAdapter.onCreateViewHolder(p0, p1 - 1)
    }
  }

  override fun onBindViewHolder(p0: RecyclerView.ViewHolder?, p1: Int) {
    if (isSectionHeaderPosition(p1)) (p0 as SectionViewHolder).title.text = sections[p1].title
    else {
      baseAdapter.onBindViewHolder(p0 as T, sectionedPositionToPosition(p1))
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (isSectionHeaderPosition(position)) SECTION_TYPE
    else baseAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1
  }

  private fun isSectionHeaderPosition(position: Int): Boolean = sections[position] != null

  fun update(sectionArray: Array<Section>) {
    sections.clear()
    sectionArray.sortedArrayWith(Comparator<Section> { o, o1 ->
      if ((o.firstPosition === o1.firstPosition)) 0
      else (if ((o.firstPosition < o1.firstPosition)) -1 else 1)
    })
    var offset = 0
    sectionArray.forEach {
      it.sectionedPosition = it.firstPosition + offset
      sections.append(it.sectionedPosition, it)
      ++offset
    }
    notifyDataSetChanged()
  }

  fun positionToSectionedPosition(position: Int): Int {
    var offset = 0
    for (i in 0..sections.size() - 1) {
      if (sections.valueAt(i).firstPosition > position) {
        break
      }
      ++offset
    }
    return position + offset
  }

  fun sectionedPositionToPosition(sectionedPosition: Int): Int {
    if (isSectionHeaderPosition(sectionedPosition)) {
      return RecyclerView.NO_POSITION
    }
    var offset = 0
    for (i in 0..(sections.size() - 1)) {
      if (sections.valueAt(i).sectionedPosition > sectionedPosition) {
        break
      }
      --offset
    }
    return sectionedPosition + offset
  }

  class Section(val firstPosition: Int, val title: CharSequence) {
    var sectionedPosition = 0
  }

  class SectionViewHolder(view: View, textId: Int) : RecyclerView.ViewHolder(view) {
    val title: TextView = view.findViewById(textId) as TextView
  }

  companion object {
    const val SECTION_TYPE = 0
  }

}