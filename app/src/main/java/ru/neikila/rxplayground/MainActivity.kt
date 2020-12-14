package ru.neikila.rxplayground

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val recycler = findViewById<RecyclerView>(R.id.items)
        recycler.adapter = MyAdapter(this)
        recycler.layoutManager = LinearLayoutManager(this)
    }

    class MyAdapter(private val context: Context) : RecyclerView.Adapter<Holder>() {

        val list = mutableListOf<Int>().apply { addAll(0 until 100) }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
            return Holder(parent.context)
        }

        override fun getItemCount(): Int = list.size

        @SuppressLint("SetTextI18n")
        override fun onBindViewHolder(holder: Holder, position: Int) {
            val itemValue = list[position]
            holder.text.text = "Item[$position] = $itemValue"
            holder.text.background = null
            holder.text.setOnLongClickListener(longClickListener(holder, itemValue.toString()))
            holder.text.setOnDragListener(dragListen(holder))
        }

        private fun longClickListener(holder: Holder, itemValue: String) = View.OnLongClickListener { v: View ->
            val intent = Intent()
            intent.putExtra("POS", holder.adapterPosition)
            intent.putExtra("VALUE", itemValue)
            val item = ClipData.Item(intent)

            val dragData = ClipData(
                itemValue,
                arrayOf(ClipDescription.MIMETYPE_TEXT_INTENT),
                item)

            val myShadow = MyDragShadowBuilder(v)

            v.startDrag(
                dragData,   // the data to be dragged
                myShadow,   // the drag shadow builder
                null,       // no need to use local data
                0           // flags (not currently used, set to 0)
            )
        }

        private fun handleDrop(from: Int, to: Int) {
            val fromVal = list[from]
            list[to] = list[to] + fromVal
            notifyItemRangeChanged(0, list.size)
            list.removeAt(from)
            notifyItemRemoved(from)
        }

        // Creates a new drag event listener
        private fun dragListen(holder: Holder) = View.OnDragListener { v, event ->
            val position = holder.adapterPosition

            // Handles each of the expected events
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // Determines if this View can accept the dragged data
                    if (position % 4 == 0) false
                        else if (event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_INTENT)) {
                        v.background = ColorDrawable(Color.BLUE)
                        v.invalidate()

                        // returns true to indicate that the View can accept the dragged data.
                        true
                    } else {
                        // Returns false. During the current drag and drop operation, this View will
                        // not receive events again until ACTION_DRAG_ENDED is sent.
                        false
                    }
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    v.background = ColorDrawable(Color.GREEN)
                    v.invalidate()
                    true
                }

                DragEvent.ACTION_DRAG_LOCATION -> true
                DragEvent.ACTION_DRAG_EXITED -> {
                    v.background = ColorDrawable(Color.BLUE)
                    v.invalidate()
                    true
                }
                DragEvent.ACTION_DROP -> {
                    val item: ClipData.Item = event.clipData.getItemAt(0)
                    val dragData = item.intent
                    Toast.makeText(context, "Dragged data is ${dragData.getStringExtra("VALUE")} of pos ${dragData.getIntExtra("POS", 0)}. Catched on pos $position", Toast.LENGTH_SHORT).show()
                    handleDrop(dragData.getIntExtra("POS", 0), position)
                    v.background = null
                    v.invalidate()

                    // Returns true. DragEvent.getResult() will return true.
                    true
                }

                DragEvent.ACTION_DRAG_ENDED -> {
                    v.background = null
                    v.invalidate()
                    val result = if (event.result) {
                        "The drop was handled. on pos: $position"
                    } else {
                        "The drop didn't work. on pos: $position"
                    }
//                    Toast.makeText(
//                        context,
//                        result,
//                        Toast.LENGTH_SHORT
//                    ).show()

                    true
                }
                else -> {
                    Log.e("DragDrop Example", "Unknown action type received by OnDragListener.")
                    false
                }
            }
        }
    }

    class Holder(context: Context) : RecyclerView.ViewHolder(
        LayoutInflater.from(context).inflate(R.layout.item, null, false)
    ) {
        val text: TextView
            get() = itemView as TextView
    }
}