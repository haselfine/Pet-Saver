package com.example.PetBoard;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.PetBoard.db.Pet;

import java.util.Collections;
import java.util.List;


public class PetListFragment extends Fragment implements PetListListener{



    public interface PetClickedListener{
        void petClicked(Pet pet);
    }

    private PetClickedListener mPetClickedListener;

    private PetViewModel mPetViewModel;
    private List<Pet> mPets;

    private static final String TAG = "WISH_LIST_FRAGMENT";

    private PetListAdapter petListAdapter;

    public PetListFragment() {

    }

    public static PetListFragment newInstance() {
        PetListFragment fragment = new PetListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        final PetViewModel petViewModel = ViewModelProviders.of(this).get(PetViewModel.class);

        final Observer<List<Pet>> petListObserver = new Observer<List<Pet>>() {
            @Override
            public void onChanged(List<Pet> pets) {
                Log.d(TAG, "Pets changed: " + pets);
                Collections.sort(pets);
                PetListFragment.this.mPets = pets;
                PetListFragment.this.petListAdapter.setPets(pets);
                PetListFragment.this.petListAdapter.notifyDataSetChanged();
            }
        };

        petViewModel.getAllPets().observe(this, petListObserver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pet_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.pet_list);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(layoutManager);
        petListAdapter = new PetListAdapter(this.getContext(), this);
        petListAdapter.setPets(mPets);
        recyclerView.setAdapter(petListAdapter);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context){
        Log.d(TAG, "onAttach");
        super.onAttach(context);

        if(context instanceof PetClickedListener){
            mPetClickedListener = (PetClickedListener) context;
            Log.d(TAG, "On attach configured listener " + mPetClickedListener);
        } else {
            throw new RuntimeException(context.toString() + " must implement PetSelectedListener");
        }
    }

    @Override
    public void onStart(){
        super.onStart();
        mPetViewModel = ViewModelProviders.of(this).get(PetViewModel.class);
    }

    @Override
    public void onListClick(int position){
        Pet pet = mPets.get(position);
        mPetClickedListener.petClicked(pet);
    }

    @Override
    public void onListLongClick(final int position){
        final Pet pet = mPets.get(position);

        if(getActivity() == null){
            return;
        }
        AlertDialog confirmDeleteDialog = new AlertDialog.Builder(getActivity())
                .setMessage(getString(R.string.delete_pet_message, mPets.get(position).getName()))
                .setTitle(R.string.delete_pet)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPetViewModel.delete(pet);
                        petListAdapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(android.R.string.cancel, null)
                .create();
        confirmDeleteDialog.show();
    }

    public void updateList(Pet pet, boolean edit) {
        if(edit){
            int petIndex = mPets.indexOf(pet);
            mPets.remove(pet);
            mPetViewModel.delete(pet);
            petListAdapter.notifyItemRemoved(petIndex);
        } else {
            mPets.add(pet);
            mPetViewModel.insert(pet);
            petListAdapter.notifyItemInserted(mPets.size()-1);
        }
    }
}


