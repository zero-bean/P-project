package com.gcu.jobshorts;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gcu.jobshorts.databinding.FragmentSearchBinding;

import java.util.ArrayList;
import java.util.List;

public class CardViewActivity extends AppCompatActivity {
    private FragmentSearchBinding activityCardViewBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCardViewBinding = DataBindingUtil.setContentView(this, R.layout.shortcard);

        List<CardModel> cardModelList = new ArrayList<>();
        cardModelList.add(new CardModel("회사A", "서울", R.drawable.company));
        cardModelList.add(new CardModel("회사B", "인천", R.drawable.company));
        cardModelList.add(new CardModel("회사C", "경기도", R.drawable.company));
        cardModelList.add(new CardModel("회사D", "강남", R.drawable.company));
        cardModelList.add(new CardModel("회사E", "성남", R.drawable.company));

        CardViewAdapter cardViewAdapter = new CardViewAdapter(cardModelList);
        // recyclerview의 adapter에 CardViewAdapter를 설정한다.
        activityCardViewBinding.recyclerView.setAdapter(cardViewAdapter);
        // recyclerview에 layoutmanager를 설정한다.
        // LinearLayoutManager을 사용하여 아이템을 수직으로 배치한다.
        activityCardViewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}