package com.example.gqqqqq.lorastop.utils;

import com.baidu.mapapi.model.LatLng;

public class LocationUtils {
        private static double EARTH_RADIUS = 6378.137;

        private static double rad(double d) {
            return d * Math.PI / 180.0;
        }

        /**
         * 通过经纬度获取距离(单位：米)
         *
         * @param latLng1
         * @param latLng2
         * @return 距离
         */
        public static double getDistance(LatLng latLng1, LatLng latLng2) {
            double radLat1 = rad(latLng1.latitude);
            double radLat2 = rad(latLng2.latitude);
            double a = radLat1 - radLat2;
            double b = rad(latLng1.longitude) - rad(latLng2.longitude);
            double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                    + Math.cos(radLat1) * Math.cos(radLat2)
                    * Math.pow(Math.sin(b / 2), 2)));
            s = s * EARTH_RADIUS;
            s = Math.round(s * 10000d) / 10000d;
            s = s * 1000;
            return s;
        }
}
