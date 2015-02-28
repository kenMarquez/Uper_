package mx.ken.devf.uper.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import mx.ken.devf.uper.R;


/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PaymentFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String PREFS_NAM = "MyPreferences";
    public static final String KEY_SET_PREFERENCE = "SET_KEY";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<String> cardsList;
    private Set<String> set;
    ArrayAdapter<String> adapter;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaymentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaymentFragment newInstance(String param1, String param2) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public PaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        setHasOptionsMenu(true);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        cardsList = new ArrayList<String>();
        inicializateSet();
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ListView lv = (ListView) view.findViewById(R.id.list_credit_cards);
        cardsList.addAll(set);
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_payment_card, R.id.tv_card, cardsList);
        lv.setAdapter(adapter);

        return view;
    }

    private void inicializateSet() {
        set = new HashSet<String>();
        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
        set = preferences.getStringSet(KEY_SET_PREFERENCE, new HashSet<String>());
        if (set.size() == 0) {
            set.add("**** 2143");
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_payment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            AlertDialog dialog = showDialog();
            dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void savePreference(String key, String value) {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public String getPreference(String key, String defaultValue) {
        SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String name = preferences.getString(key, defaultValue);
        return name;
    }

    public AlertDialog showDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialog = inflater.inflate(R.layout.dialog_add_card, null);
        final EditText editText = (EditText) dialog.findViewById(R.id.editTextDialogUserInput);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(dialog)
                // Add action buttons
                .setPositiveButton("aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String noCard = editText.getText().toString();
                        noCard = noCard.substring(noCard.length() - 4, noCard.length());
                        cardsList.add("**** " + noCard);
                        set.add("**** " + noCard);
                        Set<String> setModify = new HashSet<String>(set);
                        SharedPreferences preferences = getActivity().getSharedPreferences(PREFS_NAM, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet(KEY_SET_PREFERENCE, setModify);
                        editor.commit();
                        adapter.notifyDataSetChanged();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //LoginDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();

    }


}
