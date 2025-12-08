package com.tfb.cbit.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tfb.cbit.R;
import com.tfb.cbit.adapter.SpinningImageAdapter;
import com.tfb.cbit.interfaces.OnItemClickListener;
import com.tfb.cbit.models.SpinningImagesModel;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SpinningItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SpinningItemFragment extends Fragment implements OnItemClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static SpinningItemFragment fragment;
    public RecyclerView recy_spinning_image;
    public int pageNumber = 0;
    int questionNumber;
    Activity activity;
    ArrayList<SpinningImagesModel.Content> spinningImagesModelArrayList = new ArrayList<>();
    public View view;

    public SpinningItemFragment() {
        // Required empty public constructor
    }

    private static final String ARG_PAGE_NUMBER = "page_number";

    public SpinningItemFragment(ArrayList<SpinningImagesModel.Content> spinningImagesModelArrayList) {
    }

    public SpinningItemFragment(ArrayList<SpinningImagesModel.Content> spinningImagesModelArrayList, int questionNumber, Activity activity) {
        this.pageNumber = questionNumber - 1;
        this.questionNumber = questionNumber;
        this.activity = activity;
        this.spinningImagesModelArrayList = spinningImagesModelArrayList;
    }

    // TODO: Rename and change types and number of parameters
    public static SpinningItemFragment newInstance(int page) {
        fragment = new SpinningItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE_NUMBER, page);
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
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

    SpinningImageAdapter optionsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spinning_item, container, false);
        recy_spinning_image = view.findViewById(R.id.recy_spinning_image);
        recy_spinning_image.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        optionsAdapter = new SpinningImageAdapter(getActivity(), spinningImagesModelArrayList);
        recy_spinning_image.setAdapter(optionsAdapter);
        return view;
    }

    public String[] itemValue;

    @Override
    public void onItemClick(View view, int position) {

        if (view.getId() == R.id.chkSelect) {
        }

    }

    @Override
    public void setMenuVisibility(boolean menuVisible) {
        super.setMenuVisibility(menuVisible);
        if (optionsAdapter != null)
            optionsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        optionsAdapter.notifyDataSetChanged();
    }
}