package com.king.tv.mvp.fragment;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.king.base.adapter.ViewPagerFragmentAdapter;
import com.king.base.util.LogUtils;
import com.king.tv.R;
import com.king.tv.bean.Room;
import com.king.tv.mvp.base.BaseFragment;
import com.king.tv.mvp.presenter.RoomPresenter;
import com.king.tv.mvp.view.IRoomView;
import com.king.tv.util.DensityUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * @since 2017/3/7
 */
/*播放房间的fragment*/
public class RoomFragment extends BaseFragment<IRoomView, RoomPresenter> implements IRoomView {

  private VideoFragment videoFragment;

  /*播放的id*/
  @BindView(R.id.frameVideo)
  FrameLayout frameVideo;

  @BindView(R.id.ivBack)
  ImageView ivBack;
  @BindView(R.id.ivShare)
  ImageView ivShare;
  @BindView(R.id.tvRoomTitle)
  TextView tvRoomTitle;
  @BindView(R.id.ivFullScreen)
  ImageView ivFullScreen;
  @BindView(R.id.rlRoomInfo)
  RelativeLayout rlRoomInfo;

  @BindView(R.id.llRoomChat)
  LinearLayout llRoomChat;

  @BindView(R.id.videoContent)
  RelativeLayout videoContent;
  @BindView(R.id.tabLayout)
  TabLayout tabLayout;
  @BindView(R.id.tvFollow)
  TextView tvFollow;
  @BindView(R.id.viewPager)
  ViewPager viewPager;


  private ViewPagerFragmentAdapter viewPagerFragmentAdapter;

  private List<CharSequence> listTitle;

  private List<Fragment> listData;

  private Room room;

  private String uid;

  private AnchorInfoFragment anchorInfoFragment;

  /**
   * 静态方法,返回类对象
   * @param uid
   * @return
   */
  public static RoomFragment newInstance(String uid) {

    Bundle args = new Bundle();

    RoomFragment fragment = new RoomFragment();
    fragment.uid = uid;
    fragment.setArguments(args);

    return fragment;
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    return super.onCreateView(inflater, container, savedInstanceState);
  }


  @Override
  public int getRootViewId() {
    return R.layout.fragment_room;
  }

  @Override
  public void initUI() {

    updateVideoLayoutParams();

    listTitle = new ArrayList<>();

    listTitle.add(getText(R.string.room_chat));
    listTitle.add(getText(R.string.room_ranking));
    listTitle.add(getText(R.string.room_anchor));
        /*集合*/
    listData = new ArrayList<>();
    listData.add(ChatFragment.newInstance());/*聊天*/
    listData.add(RankFragment.newInstance());/*排行*/
    anchorInfoFragment = AnchorInfoFragment.newInstance(room);/*主播信息*/
    listData.add(anchorInfoFragment);

    viewPagerFragmentAdapter = new ViewPagerFragmentAdapter(getFragmentManager(), listData,
        listTitle);
    viewPager.setAdapter(viewPagerFragmentAdapter);
        /*tab和ViewPager进行联动*/
    tabLayout.setupWithViewPager(viewPager);

    //        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
    //            @Override
    //            public void onTabSelected(TabLayout.Tab tab) {
    //                if(tab.getText().equals(getText(R.string.room_anchor))){
    //                    anchorInfoFragment.onUpdateAnchor(room);
    //                }
    //            }
    //
    //            @Override
    //            public void onTabUnselected(TabLayout.Tab tab) {
    //
    //            }
    //
    //            @Override
    //            public void onTabReselected(TabLayout.Tab tab) {
    //
    //            }
    //        });
  }

  /**
   * 跟新播放框架的大小
   */
  public void updateVideoLayoutParams() {
        /*videocontent,最上面的大relativelayout的播放id*/
    ViewGroup.LayoutParams lp = videoContent.getLayoutParams();

    if (isLandscape()) {
      lp.height = DensityUtil.getDisplayMetrics(context).heightPixels;
    } else {
      lp.height = (int) (DensityUtil.getDisplayMetrics(context).widthPixels / 16.0f * 9.0f);
    }

    videoContent.setLayoutParams(lp);
  }


  /*=======Preseter层执行逻辑=============*/
  @Override
  public void initData() {
    getPresenter().enterRoom(uid);
  }


  /**
   * 房间的逻辑都放入了presenter层
   * @return
   */
  @Override
  public RoomPresenter createPresenter() {
    return new RoomPresenter(getApp());
  }
    /*=====Preserter结束=========*/


  /**
   * 进入房间干的
   * @param room
   */
  @Override
  public void enterRoom(Room room) {
    this.room = room;
    LogUtils.d("room");
    anchorInfoFragment.onUpdateAnchor(room);/*根据不同的房间跟新主播信息*/
    viewPagerFragmentAdapter.notifyDataSetChanged();
  }


  /**
   * 往播放布局中填入播放器
   * @param url
   */
  @Override
  public void playUrl(String url) {
    LogUtils.d("playUrl:" + url);
    if (videoFragment == null) {
      videoFragment = VideoFragment.newInstance(url, false);
    }
    replaceChildFragment(R.id.frameVideo, videoFragment);
  }

  @Override
  public void showProgress() {

  }

  @Override
  public void onCompleted() {

  }

  @Override
  public void onError(Throwable e) {

  }

  //-------------------------------

  /*重写v4的fragment,横竖屏切换的时候调用*/
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
        /*横屏*/
    if (isLandscape()) {
      llRoomChat.setVisibility(View.GONE);/*下的聊天框隐藏*/
      ivFullScreen.setVisibility(View.GONE);/*全屏的图片隐藏*/
    } else {
      llRoomChat.setVisibility(View.VISIBLE);
      ivFullScreen.setVisibility(View.VISIBLE);
    }
    updateVideoLayoutParams();

  }

  //-------------------------------

  public void clickFrameVideo() {

  }

  public void clickBack() {
    if (isLandscape()) {
      getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } else {
      finish();
    }
  }

  public void clickShare() {

  }

  /**
   * 判断int是否相同,TRUE为横屏
   * @return
   */
  public boolean isLandscape() {
    return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
  }

  /**
   * 切换全屏的方法
   */
  public void clickFullScreen() {
    if (isLandscape()) {
          /*切换为竖屏,这里获得的activity是ContentActivity*/
      getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    } else {
      getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
  }

  public void clickFollow() {
    startLogin();
  }

  @OnClick({R.id.frameVideo, R.id.ivBack, R.id.ivShare, R.id.ivFullScreen, R.id.videoContent,
      R.id.tvFollow})
  public void onClick(View view) {
    switch (view.getId()) {
            /*播放框最外面的relativelayout*/
      case R.id.videoContent:
                /*点击第一层的帧布局*/
      case R.id.frameVideo:
        clickFrameVideo();
        break;
            /*返回图片*/
      case R.id.ivBack:
        clickBack();
        break;
            /*分享图*/
      case R.id.ivShare:
        clickShare();
        break;

            /*全屏*/
      case R.id.ivFullScreen:
        clickFullScreen();
        break;

            /*关注*/
      case R.id.tvFollow:
        clickFollow();
        break;
    }
  }
}
