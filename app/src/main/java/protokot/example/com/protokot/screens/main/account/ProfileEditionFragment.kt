package protokot.example.com.protokot.screens.main.account

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.fragment_profile.*
import protokot.example.com.protokot.R
import protokot.example.com.protokot.data.UserProfile
import protokot.example.com.protokot.screens.base.AbstractFragment
import kotlinx.android.synthetic.main.fragment_profile_edition.*
/**
 * Created by Cardiweb on 24/01/2018.
 */
class ProfileEditionFragment : AbstractFragment() {

    override fun getContentView() = R.layout.fragment_profile_edition

    companion object {
        fun newInstance(): ProfileEditionFragment {
            return ProfileEditionFragment()
        }
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstnameField.setText("John")
        lastnameField.setText("Doe")
        loginField.setText("john.doe@test.test")
    }
}