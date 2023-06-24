
package danilo.jovanovic.shoppinglist;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // The fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button login_button, home_button;

    private EditText login_username, login_password;
    private DatabaseHelper db;
    private static String LOGIN_URL = MainActivity.BASE_URL + "/login";
    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new DatabaseHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        login_button = view.findViewById(R.id.frag_login_button);
        home_button = view.findViewById(R.id.frag_login_home_button);

        login_button.setOnClickListener(this);
        home_button.setOnClickListener(this);

        login_username = view.findViewById(R.id.frag_login_username);
        login_password = view.findViewById(R.id.frag_login_password);

        return view;
    }

    public void onClick(View view) {
        if (view.getId() == R.id.frag_login_button) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        JSONObject userJson = new JSONObject();
                        userJson.put("username", login_username.getText().toString());
                        userJson.put("password", login_password.getText().toString());

                        HttpHelper httpHelper = new HttpHelper();
                        boolean login_success = httpHelper.postJSONObjectFromURL(LOGIN_URL, userJson);

                        if(login_success){
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);

                                    Bundle bundle = new Bundle();
                                    bundle.putString("username", login_username.getText().toString());

                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });
                        }else {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getActivity().getApplicationContext(), "Username does not exist, please register.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }catch (IOException | JSONException e){
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if(view.getId() == R.id.frag_login_home_button){
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }
}
