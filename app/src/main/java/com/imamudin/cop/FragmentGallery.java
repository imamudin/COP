package com.imamudin.cop;

/**
 * Created by agung on 19/02/2016.
 */
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

public class FragmentGallery extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    LinearLayout ln_interogasi, ln_lit_dokumen, ln_observasi;

    private OnFragmentInteractionListener mListener;

    public FragmentGallery() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentOne.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentGallery newInstance(String param1, String param2) {
        FragmentGallery fragment = new FragmentGallery();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_gallery,container,false);

        ln_interogasi     = (LinearLayout)v.findViewById(R.id.ln_interogasi);
        ln_interogasi.setOnClickListener(LayoutListener);
        ln_lit_dokumen     = (LinearLayout)v.findViewById(R.id.ln_lit_dokumen);
        ln_lit_dokumen.setOnClickListener(LayoutListener);
        ln_observasi     = (LinearLayout)v.findViewById(R.id.ln_observasi);
        ln_observasi.setOnClickListener(LayoutListener);


        return v;
    }

    View.OnClickListener  LayoutListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(view==ln_interogasi){
                Intent berkas1 = new Intent(getContext(), BerkasSatu.class);

                startActivity(berkas1);
//                FragmentManager childFragMan = getChildFragmentManager();
//
//                FragmentTransaction childFragTrans = childFragMan.beginTransaction();
//                FragmentImport fragB = new FragmentImport();
//                childFragTrans.replace(R.id.fragment_gallery2, fragB);
//
//                childFragTrans.addToBackStack("B");
//                childFragTrans.commit();
            }else if(view==ln_lit_dokumen){
                Toast.makeText(getContext(),"ln_2",Toast.LENGTH_LONG).show();
            }else if(view==ln_observasi){
                Toast.makeText(getContext(),"ln_3",Toast.LENGTH_LONG).show();
            }
        }
    };

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}