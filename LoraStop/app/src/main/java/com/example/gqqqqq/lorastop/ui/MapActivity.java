package com.example.gqqqqq.lorastop.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiBoundSearchOption;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiDetailSearchResult;
import com.baidu.mapapi.search.poi.PoiIndoorResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.poi.PoiSortType;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.mapframework.nirvana.Utils;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManagerFactory;
import com.baidu.navisdk.adapter.IBNRoutePlanManager;
import com.baidu.navisdk.adapter.IBNTTSManager;
import com.baidu.navisdk.adapter.IBaiduNaviManager;
import com.baidu.platform.comapi.map.PoiOverlay;
import com.example.gqqqqq.lorastop.MainActivity;
import com.example.gqqqqq.lorastop.R;
import com.example.gqqqqq.lorastop.adapter.MyBaseExpandableListAdapter;
import com.example.gqqqqq.lorastop.entity.Group;
import com.example.gqqqqq.lorastop.entity.Item;

import com.example.gqqqqq.lorastop.utils.L;
import com.example.gqqqqq.lorastop.utils.LocationUtils;
import com.example.gqqqqq.lorastop.utils.MyOrientationListener;
import com.example.gqqqqq.lorastop.utils.NormalUtils;
import com.example.gqqqqq.lorastop.utils.PermissionHelper;
import com.example.gqqqqq.lorastop.utils.PermissionInterface;
import com.example.gqqqqq.lorastop.utils.ShareUtils;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends BaseActivity implements PermissionInterface, View.OnClickListener, OnGetSuggestionResultListener {
    
    private MapView mMapView;
    private BaiduMap mBaiduMAp;
    //自定义图标
    private BitmapDescriptor mIconLocation;
    private MyOrientationListener myOrientationListener;
    private float mCurrentX;
    //定位图层显示方式
    private MyLocationConfiguration.LocationMode locationMode;
    //控件
    private ImageView img_location;
    private View li_mark;
    private TextView tv_num;
    private TextView tv_empty;
    private Button bt_navi;
    //导航起点终点经纬度
    private LatLng mLastLocationData;//我的位置
    private LatLng mDestLocationData;//终点位置
    private LatLng mSelectLocationData;//选择的位置
    private LatLng latLng;
    //开始导航按钮
    //private Button btn_nav;
    //定位相关
    public LocationClient mLocationClient = null;
    private BDAbstractLocationListener myListener = new MyLocationListener();
    //导航相关
    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";
    static final String ROUTE_PLAN_NODE = "routePlanNode";
    private String mSDCardPath = null;
    private static final String[] authBaseArr = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final int authBaseRequestCode = 1;
    private boolean hasInitSuccess = false;
    private BNRoutePlanNode mStartNode = null;

    // SuggestionSearch建议查询类
    private SuggestionSearch mSuggestionSearch;
    private int load_Index = 0;
    private List<LatLng> latLnglist = null;
    //	自动填充的text
    private AutoCompleteTextView keyWorldsView = null;
    private ArrayAdapter<String> sugAdapter = null;
    //权限相关
    private PermissionHelper mPermissionHelper;

    //listview
    private ArrayList<Group> gData = null;
    private ArrayList<ArrayList<Item>> iData = null;
    private ArrayList<Item> lData = null;
    private Context mContext;
    private ExpandableListView exlist_lol;
    private MyBaseExpandableListAdapter myAdapter = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        mMapView = (MapView)findViewById(R.id.mMapView);
        img_location = (ImageView)findViewById(R.id.img_location);
        img_location.setOnClickListener(this);
        li_mark = findViewById(R.id.li_mark);
        li_mark.setVisibility(View.GONE);
        tv_num = (TextView)findViewById(R.id.tv_num);
        tv_empty = (TextView)findViewById(R.id.tv_empty);
        bt_navi = (Button)findViewById(R.id.bt_navi);

        //btn_nav = (Button)findViewById(R.id.btn_nav);
        //btn_nav.setOnClickListener(this);
        //初始化并发起权限申请

        mPermissionHelper = new PermissionHelper(this,this);

        mPermissionHelper.requestPermissions();

        //initView();
        /*
        * 1.定位
        * 2.绘制图层
        * */
    }

    private void initView() {
        //listview
        mContext = MapActivity.this;
        exlist_lol = (ExpandableListView) findViewById(R.id.exlist_lol);

        //initListView();

        mBaiduMAp = mMapView.getMap();
        // 开启定位图层
        mBaiduMAp.setMyLocationEnabled(true);
        // 将这些添加到地图中去(poi搜索)
        keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
        sugAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line);
        keyWorldsView.setAdapter(sugAdapter);
        // 实例化建议查询类
        mSuggestionSearch = SuggestionSearch.newInstance();
        // 注册建议查询事件监听
        mSuggestionSearch.setOnGetSuggestionResultListener(this);
        /**

         * 当输入关键字变化时，动态更新建议列表

         */
        keyWorldsView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int start, int before,
                                      int count) {
                if (cs.length() <= 0) {
                    return;
                }
                /**
                 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
                 */
                mSuggestionSearch
                        .requestSuggestion((new SuggestionSearchOption())
                                .keyword(cs.toString()).city("成都"));
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }
        });
        keyWorldsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(MapActivity.this,""+latLnglist.get(position).longitude,Toast.LENGTH_SHORT).show();
                selectMyLocation(latLnglist.get(position));
            }
        });


        mBaiduMAp.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i=0;i<lData.size();i++){
                    if(lData.get(i).getMarker() == marker){
                        Toast.makeText(MapActivity.this,"name"+lData.get(i).getiName(),Toast.LENGTH_LONG).show();
                        mDestLocationData = lData.get(i).getLatLng();
                        tv_num.setText(lData.get(i).getiNum());
                        tv_empty.setText(lData.get(i).getiEmpty());
                        li_mark.setVisibility(View.VISIBLE);
                        bt_navi.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                routeplanToNavi();
                            }
                        });
                    }
                }
                return true;
            }
        });
        mBaiduMAp.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                li_mark.setVisibility(View.GONE);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMAp.setOnMapLongClickListener(new BaiduMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mSelectLocationData = latLng;
                Toast.makeText(MapActivity.this,"已为您显示周围停车场",Toast.LENGTH_SHORT).show();
                initListView();
            }
        });

        //声明LocationClient类
        mLocationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        mLocationClient.registerLocationListener(myListener);

        LocationClientOption option = new LocationClientOption();
        initLocation(option);

        mLocationClient.setLocOption(option);
        //开启定位
        //mLocationClient.start();
        //mLocationClient.stop();

        //初始化导航相关
        if (initDirs()) {
            //Toast.makeText(MapActivity.this, "进入初始化", Toast.LENGTH_LONG).show();
           initNavi();
        }
    }

    private void initListView() {
        //数据准备
        gData = new ArrayList<Group>();
        iData = new ArrayList<ArrayList<Item>>();
        gData.add(new Group("周边停车场"));
        //拼接url
        String url = "http://118.24.39.197/pl/";
        //请求数据（Json）
        RxVolley.get(url, new HttpCallback() {
            @Override
            public void onSuccess(String t) {
                L.i("Json"+t);
                //解析Json
                parsingJson(t);
            }
        });

        myAdapter = new MyBaseExpandableListAdapter(gData,iData,mContext);
        exlist_lol.setAdapter(myAdapter);

        //为列表设置点击事件
        exlist_lol.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(mContext, "你点击了：" + iData.get(groupPosition).get(childPosition).getiName(), Toast.LENGTH_SHORT).show();
                mDestLocationData = iData.get(groupPosition).get(childPosition).getLatLng();
                routeplanToNavi();
                return true;
            }
        });

    }

    private void parsingJson(String t) {
        lData = new ArrayList<Item>();
        mBaiduMAp.clear();
        try {
            JSONObject jsonObject = new JSONObject(t);
            JSONArray jsonArray = jsonObject.getJSONArray("results");
            for (int i=0;i<jsonArray.length();i++){
                JSONObject json = (JSONObject)jsonArray.get(i);
                latLng = new LatLng(Double.parseDouble(json.getString("latitude")),Double.parseDouble(json.getString("longitude")));
                double ss = LocationUtils.getDistance(latLng,mSelectLocationData);
                //Toast.makeText(MapActivity.this, "距离"+ss, Toast.LENGTH_SHORT).show();
                if(ss/1000<5) {
                    lData.add(new Item(json.getString("name"),
                            json.getString("cwnum"),
                            json.getString("shengyu"),
                            latLng,addDestInfoOverlay(latLng)));
                    //L.i(json.getString("longitude"));
                    //L.i(json.getString("latitude"));
                }
            }
            if(lData.isEmpty()){
                Toast.makeText(MapActivity.this, "周围无Lora停车场",Toast.LENGTH_SHORT).show();
            }
            iData.add(lData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        Toast.makeText(MapActivity.this, "返回对", Toast.LENGTH_LONG).show();
        return true;
    }

    private boolean hasBasePhoneAuth() {
        PackageManager pm = this.getPackageManager();
        for (String auth : authBaseArr) {
            if (pm.checkPermission(auth, this.getPackageName()) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initNavi() {
        //申请权限
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (!hasBasePhoneAuth()) {
                this.requestPermissions(authBaseArr, authBaseRequestCode);
                return;
            }
        }

        BaiduNaviManagerFactory.getBaiduNaviManager().init(this,
                mSDCardPath, APP_FOLDER_NAME, new IBaiduNaviManager.INaviInitListener() {
                    String result;
                    @Override
                    public void onAuthResult(int status, String msg) {
                        if (0 == status) {
                            result = "key校验成功!";
                        } else {
                            result = "key校验失败, " + msg;
                        }
                        Toast.makeText(MapActivity.this, result, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void initStart() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void initSuccess() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                        hasInitSuccess = true;

                        // 初始化tts
                        initTTS();
                    }

                    @Override
                    public void initFailed() {
                        Toast.makeText(MapActivity.this, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                    }

                });


    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void initTTS() {
        // 使用内置TTS
        BaiduNaviManagerFactory.getTTSManager().initTTS(MapActivity.this,
                getSdcardDir(), APP_FOLDER_NAME, NormalUtils.getTTSAppID());

        // 注册同步内置tts状态回调
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedListener(
                new IBNTTSManager.IOnTTSPlayStateChangedListener() {
                    @Override
                    public void onPlayStart() {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayStart");
                    }

                    @Override
                    public void onPlayEnd(String speechId) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayEnd");
                    }

                    @Override
                    public void onPlayError(int code, String message) {
                        Log.e("BNSDKDemo", "ttsCallback.onPlayError");
                    }
                }
        );

        // 注册内置tts 异步状态消息
        BaiduNaviManagerFactory.getTTSManager().setOnTTSStateChangedHandler(
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("BNSDKDemo", "ttsHandler.msg.what=" + msg.what);
                    }
                }
        );
    }

    private void routeplanToNavi() {
        if (!hasInitSuccess) {
            Toast.makeText(MapActivity.this, "还未初始化!", Toast.LENGTH_SHORT).show();
        }
        final int coType = BNRoutePlanNode.CoordinateType.BD09LL;

        BNRoutePlanNode sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", "百度大厦", coType);
        BNRoutePlanNode eNode = new BNRoutePlanNode(104.05148, 30.69821, "北京天安门", "北京天安门", coType);
        //mDestLocationData = new LatLng(104.05148,30.69821);
        //Toast.makeText(MapActivity.this,"sss"+mDestLocationData.longitude,Toast.LENGTH_SHORT).show();
        //Toast.makeText(MapActivity.this,"aaa"+mLastLocationData.longitude,Toast.LENGTH_SHORT).show();
        sNode = new BNRoutePlanNode(mLastLocationData.longitude,mLastLocationData.latitude,"我的位置","我的位置",coType);
        eNode = new BNRoutePlanNode(mDestLocationData.longitude, mDestLocationData.latitude, "目标位置", "目标位置", coType);

        mStartNode = sNode;

        List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
        list.add(sNode);
        list.add(eNode);

        BaiduNaviManagerFactory.getRoutePlanManager().routeplanToNavi(
                list,
                IBNRoutePlanManager.RoutePlanPreference.ROUTE_PLAN_PREFERENCE_DEFAULT,
                null,
                new Handler(Looper.getMainLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_START:
                                Toast.makeText(MapActivity.this, "算路开始", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_SUCCESS:
                                Toast.makeText(MapActivity.this, "算路成功", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_FAILED:
                                Toast.makeText(MapActivity.this, "算路失败", Toast.LENGTH_SHORT)
                                        .show();
                                break;
                            case IBNRoutePlanManager.MSG_NAVI_ROUTE_PLAN_TO_NAVI:
                                Toast.makeText(MapActivity.this, "算路成功准备进入导航", Toast.LENGTH_SHORT)
                                        .show();
                                Intent intent = new Intent(MapActivity.this,
                                        GuideActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(ROUTE_PLAN_NODE, mStartNode);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                break;
                            default:
                                // nothing
                                break;
                        }
                    }
                });
    }

    private Marker addDestInfoOverlay(LatLng latLng) {
        //定义Maker坐标点
        LatLng point = latLng;
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.maker);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .perspective(true);
        //在地图上添加Marker，并显示
        Marker marker= (Marker)mBaiduMAp.addOverlay(option);
        return marker;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_location:
                getMyLocation();
                //mLocationClient.start();
                //if(mLastLocationData!=null){
                    //移动到我的位置
                    //MapStatusUpdate mapUpdata = MapStatusUpdateFactory.zoomTo(18);
                    //mBaiduMAp.setMapStatus(mapUpdata);
                    //MapStatusUpdate mapLating = MapStatusUpdateFactory.newLatLng(mLastLocationData);
                    //mBaiduMAp.setMapStatus(mapLating);
                    //绘制图层
                    //mBaiduMAp.clear();
                    //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
                    //OverlayOptions option = new MarkerOptions().position(mLastLocationData).icon(bitmap);
                    //mBaiduMAp.addOverlay(option);
                //}
                break;
            //case R.id.btn_nav:
            //    if (BaiduNaviManagerFactory.getBaiduNaviManager().isInited()) {
             //       routeplanToNavi();
             //   }
             //   break;
        }
    }

    private void getMyLocation() {
        //移动到我的位置
        keyWorldsView.setText("");
        MapStatusUpdate mapUpdata = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMAp.setMapStatus(mapUpdata);
        LatLng latLng2=mLastLocationData;
        mSelectLocationData = mLastLocationData;
        MapStatusUpdate msu= MapStatusUpdateFactory.newLatLng(latLng2);
        mBaiduMAp.setMapStatus(msu);
        initListView();
    }

    private void selectMyLocation(LatLng latLng) {
        //移动到选择的位置
        MapStatusUpdate mapUpdata = MapStatusUpdateFactory.zoomTo(18);
        mBaiduMAp.setMapStatus(mapUpdata);
        mSelectLocationData = latLng;
        MapStatusUpdate msu= MapStatusUpdateFactory.newLatLng(latLng);
        mBaiduMAp.setMapStatus(msu);
        initListView();
    }

    @Override
    public void onGetSuggestionResult(SuggestionResult suggestionResult) {
        if (suggestionResult == null || suggestionResult.getAllSuggestions() == null) {
            return;
        }

        List<String> suggest = new ArrayList<>();
        latLnglist = new ArrayList<>();
        for (SuggestionResult.SuggestionInfo info : suggestionResult.getAllSuggestions()) {
            if (info.key != null) {
                suggest.add(info.key);
                latLnglist.add(info.pt);
            }
        }

        sugAdapter = new ArrayAdapter<>(MapActivity.this, android.R.layout.simple_dropdown_item_1line,
                suggest);
        keyWorldsView.setAdapter(sugAdapter);
        sugAdapter.notifyDataSetChanged();
    }

    private class MyLocationListener extends BDAbstractLocationListener {
        //定位请求回调接口
        private boolean isFirstIn=true;
        @Override
        public void onReceiveLocation(BDLocation location) {

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(location.getTime());    //获取定位时间

            sb.append("\nerror code : ");
            sb.append(location.getLocType());    //获取类型类型

            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());    //获取纬度信息

            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(location.getRadius());    //获取定位精准度



            if (location.getLocType() == BDLocation.TypeGpsLocation){

                // GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());    // 单位：公里每小时

                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());    //获取卫星数

                sb.append("\nheight : ");
                sb.append(location.getAltitude());    //获取海拔高度信息，单位米

                sb.append("\ndirection : ");
                sb.append(location.getDirection());    //获取方向信息，单位度

                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\ndescribe : ");
                sb.append("gps定位成功");


            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation){

                // 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());    //获取地址信息

                sb.append("\noperationers : ");
                sb.append(location.getOperators());    //获取运营商信息

                sb.append("\ndescribe : ");
                sb.append("网络定位成功");

            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {

                // 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");

            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");

            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {

                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");

            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {

                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");


            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());    //位置语义化信息
            List<Poi> list = location.getPoiList();    // POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }

            Log.i("BaiduLocationApiDem", sb.toString());

            //移动到我的位置
            //MapStatusUpdate mapUpdata = MapStatusUpdateFactory.zoomTo(18);
            //mBaiduMAp.setMapStatus(mapUpdata);
            //开始移动
            mLastLocationData = new LatLng(location.getLatitude(),location.getLongitude());
            //MapStatusUpdate mapLating = MapStatusUpdateFactory.newLatLng(mLastLocationData);
            //mBaiduMAp.setMapStatus(mapLating);
            //绘制图层
            //mBaiduMAp.clear();
            //LatLng point = mLastLocationData;
            //BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);
            //OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
            //mBaiduMAp.addOverlay(option);

            MyLocationData data= new MyLocationData.Builder()
                    .direction(mCurrentX)//设定图标方向
                    .accuracy(location.getRadius())//getRadius 获取定位精度,默认值0.0f
                    .latitude(location.getLatitude())//百度纬度坐标
                    .longitude(location.getLongitude())//百度经度坐标
                    .build();
            //设置定位数据, 只有先允许定位图层后设置数据才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMAp.setMyLocationData(data);
            //配置定位图层显示方式,三个参数的构造器
            /*
             * 1.定位图层显示模式
             * 2.是否允许显示方向信息
             * 3.用户自定义定位图标
             *
             * */
            MyLocationConfiguration configuration
                    =new MyLocationConfiguration(locationMode,true,mIconLocation);
            //设置定位图层配置信息，只有先允许定位图层后设置定位图层配置信息才会生效，参见 setMyLocationEnabled(boolean)
            mBaiduMAp.setMyLocationConfiguration(configuration);
            //判断是否为第一次定位,是的话需要定位到用户当前位置
            if(isFirstIn)
            {
                //移动到我的位置
                MapStatusUpdate mapUpdata = MapStatusUpdateFactory.zoomTo(18);
                mBaiduMAp.setMapStatus(mapUpdata);
                //地理坐标基本数据结构
                LatLng latLng=new LatLng(location.getLatitude(),location.getLongitude());
                //描述地图状态将要发生的变化,通过当前经纬度来使地图显示到该位置
                MapStatusUpdate msu= MapStatusUpdateFactory.newLatLng(latLng);
                //设置当前选择位置
                mSelectLocationData = latLng;
                //改变地图状态
                mBaiduMAp.setMapStatus(msu);
                //初始化list_view
                initListView();
                isFirstIn=false;
                Toast.makeText(MapActivity.this, location.getAddrStr(), Toast.LENGTH_SHORT).show();
            }

            //定位停止
            //mLocationClient.stop();
        }

        /**
         * 回调定位诊断信息，开发者可以根据相关信息解决定位遇到的一些问题
         * 自动回调，相同的diagnosticType只会回调一次
         *
         * @param locType           当前定位类型
         * @param diagnosticType    诊断类型（1~9）
         * @param diagnosticMessage 具体的诊断信息释义
         */
        public void onLocDiagnosticMessage(int locType, int diagnosticType, String diagnosticMessage) {

            if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_GPS) {

                //建议打开GPS

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_BETTER_OPEN_WIFI) {

                //建议打开wifi，不必连接，这样有助于提高网络定位精度！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_LOC_PERMISSION) {

                //定位权限受限，建议提示用户授予APP定位权限！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CHECK_NET) {

                //网络异常造成定位失败，建议用户确认网络状态是否异常！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_CLOSE_FLYMODE) {

                //手机飞行模式造成定位失败，建议用户关闭飞行模式后再重试定位！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_INSERT_SIMCARD_OR_OPEN_WIFI) {

                //无法获取任何定位依据，建议用户打开wifi或者插入sim卡重试！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_NEED_OPEN_PHONE_LOC_SWITCH) {

                //无法获取有效定位依据，建议用户打开手机设置里的定位开关后重试！

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_SERVER_FAIL) {

                //百度定位服务端定位失败
                //建议反馈location.getLocationID()和大体定位时间到loc-bugs@baidu.com

            } else if (diagnosticType == LocationClient.LOC_DIAGNOSTIC_TYPE_FAIL_UNKNOWN) {

                //无法获取有效定位依据，但无法确定具体原因
                //建议检查是否有安全软件屏蔽相关定位权限
                //或调用重新启动后重试！

            }
        }



    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(mPermissionHelper.requestPermissionsResult(requestCode, permissions, grantResults)){

            //权限请求结果，并已经处理了该回调

            return;

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }



    @Override
    public int getPermissionsRequestCode() {

        //设置权限请求requestCode，只有不跟onRequestPermissionsResult方法中的其他请求码冲突即可。

        return 10000;

    }



    @Override
    public String[] getPermissions() {

        //设置该界面所需的全部权限

        return new String[]{

                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE


        };

    }



    @Override
    public void requestPermissionsSuccess() {

        //权限请求用户已经全部允许
        Toast.makeText(this,"已获得权限",Toast.LENGTH_SHORT).show();
        initView();

    }



    @Override
    public void requestPermissionsFail() {

        //权限请求不被用户允许。可以提示并退出或者提示权限的用途并重新发起权限申请。

        finish();

    }

    private void initLocation(LocationClientOption option){

        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        /*
        可选，设置定位模式，默认高精度
        LocationMode.Hight_Accuracy：高精度；
        LocationMode. Battery_Saving：低功耗；
        LocationMode. Device_Sensors：仅使用设备；
        */

        option.setCoorType("BD09ll");
        //可选，设置返回经纬度坐标类型，默认GCJ02
        //GCJ02：国测局坐标；
        //BD09ll：百度经纬度坐标；
        //BD09：百度墨卡托坐标；
        //海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标

        option.setScanSpan(1000);
        //可选，设置发起定位请求的间隔，int类型，单位ms
        //如果设置为0，则代表单次定位，即仅定位一次，默认为0
        //如果设置非0，需设置1000ms以上才有效

        option.setOpenGps(true);
        //可选，设置是否使用gps，默认false
        //使用高精度和仅用设备两种定位模式的，参数必须设置为true

        option.setLocationNotify(true);
        //可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false

        option.setIgnoreKillProcess(true);
        //可选，定位SDK内部是一个service，并放到了独立进程。
        //设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)

        option.SetIgnoreCacheException(false);
        //可选，设置是否收集Crash信息，默认收集，即参数为false

        option.setWifiCacheTimeOut(5*60*1000);
        //可选，V7.2版本新增能力
        //如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位

        option.setEnableSimulateGps(false);
        //可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false

        option.setIsNeedAddress(true);
        //可选，是否需要地址信息，默认为不需要，即参数为false
        //如果开发者需要获得当前点的地址信息，此处必须为true
        /*
        mLocationClient为第二步初始化过的LocationClient对象
        需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明
        */
        locationMode= MyLocationConfiguration.LocationMode.NORMAL;

        //初始化图标,BitmapDescriptorFactory是bitmap 描述信息工厂类，在使用该类方法之前请确保已经调用了 SDKInitializer.initialize(Context) 函数以提供全局 Context 信息。
        mIconLocation= BitmapDescriptorFactory
                .fromResource(R.drawable.arrow);

        myOrientationListener=new MyOrientationListener(MapActivity.this);

        myOrientationListener.setOnOrientationListener(new MyOrientationListener.OnOrientationListener() {
            @Override
            public void onOrientationChanged(float x) {
                mCurrentX=x;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //开启定位
        mBaiduMAp.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
        {
            mLocationClient.start();
        }
        myOrientationListener.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位
        mBaiduMAp.setMyLocationEnabled(false);
        mLocationClient.stop();
        myOrientationListener.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        mSuggestionSearch.destroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.id_map_common:
                mBaiduMAp.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.id_map_site:
                mBaiduMAp.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.id_map_traffic:
                if(mBaiduMAp.isTrafficEnabled())
                {
                    mBaiduMAp.setTrafficEnabled(false);
                    item.setTitle("实时交通(off)");
                }else
                {
                    mBaiduMAp.setTrafficEnabled(true);
                    item.setTitle("实时交通(on)");
                }
                break;
            case R.id.id_map_mlocation:
                getMyLocation();
                break;
            case R.id.id_map_model_common:
                //普通模式
                locationMode= MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.id_map_model_following:
                //跟随模式
                locationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.id_map_model_compass:
                //罗盘模式
                locationMode= MyLocationConfiguration.LocationMode.COMPASS;
                break;



        }
        return super.onOptionsItemSelected(item);
    }
}
