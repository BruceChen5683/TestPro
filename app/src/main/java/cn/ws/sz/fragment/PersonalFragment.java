package cn.ws.sz.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.ws.sz.R;
import cn.ws.sz.activity.AboutActivity;
import cn.ws.sz.activity.CollectActivity;
import cn.ws.sz.activity.LookHistroryActivity;
import cn.ws.sz.activity.MoneyActivity;
import cn.ws.sz.activity.ProxyActivity;
import cn.ws.sz.activity.ServiceActivity;


public class PersonalFragment extends Fragment implements View.OnClickListener{
    private RelativeLayout money;
    private RelativeLayout apply;
    private RelativeLayout service;
    private RelativeLayout about;

    private TextView tvTitle;
    private RelativeLayout rlCollect,rlLookHistory;
    public PersonalFragment() {
        // Required empty public constructor
    }


    public static PersonalFragment newInstance(){
        return new PersonalFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        init(view);
        return view;
    }

    private void init(View view) {
        money = (RelativeLayout) view.findViewById(R.id.money);
        apply = (RelativeLayout) view.findViewById(R.id.apply);
        service = (RelativeLayout) view.findViewById(R.id.service);
        about = (RelativeLayout) view.findViewById(R.id.about);
        rlCollect = (RelativeLayout) view.findViewById(R.id.rlCollect);
        rlLookHistory = (RelativeLayout) view.findViewById(R.id.rlLookHistory);


        tvTitle = (TextView) view.findViewById(R.id.title_value);
        tvTitle.setText("我的万商");

        money.setOnClickListener(this);
        apply.setOnClickListener(this);
        service.setOnClickListener(this);
        about.setOnClickListener(this);
        rlCollect.setOnClickListener(this);
        rlLookHistory.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        switch (v.getId()){
            case R.id.money:
                intent.setClass(getActivity(), MoneyActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.apply:
                intent.setClass(getActivity(), ProxyActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.service:
                intent.setClass(getActivity(), ServiceActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.about:
                intent.setClass(getActivity(), AboutActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.rlCollect:
                intent.setClass(getActivity(), CollectActivity.class);
                getActivity().startActivity(intent);
                break;
            case R.id.rlLookHistory:
                intent.setClass(getActivity(), LookHistroryActivity.class);
                getActivity().startActivity(intent);
                break;
            default:break;


        }
    }
}
