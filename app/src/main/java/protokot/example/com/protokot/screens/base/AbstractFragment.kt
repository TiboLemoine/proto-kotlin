package protokot.example.com.protokot.screens.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup

/**
 * Base fragment class
 */
abstract class AbstractFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?) = inflater?.inflate(getContentView(), container, false)

    abstract fun getContentView() : Int
}