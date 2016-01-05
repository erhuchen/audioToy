package utils;

import java.util.ArrayList;

/**
 * 根据初始化传入的值，计算出分配好的距离，高度，方位信息
 * 
 * @author erhuchen
 *
 */
public class ParameterParser {


	private boolean isColocWise = true;
	private int start_dist;
	private int start_elev;
	private int start_azi;
	private int end_dist;
	private int end_elev;
	private int end_azi;
	public ArrayList<Integer> dists;
	public ArrayList<Integer> elevs;
	public ArrayList<Integer> azis;
	
	
	/**
	 * 初始化方法，初始化之后即可以访问其dists、elevs、azis三个属性
	 * 这三个属性长度一样，代表了运动轨迹对应的参数坐标
	 * @param isFix  是否保持方位角固定，如果为true，方位角信息start_azi为有效，end_azi无效
	 * @param isColocWise 是否顺时针
	 * @param start_dist 起始距离
	 * @param start_elev 起始高度
	 * @param start_azi  起始方位
	 * @param end_dist   终止距离
	 * @param end_elev   终止高度
	 * @param end_azi    终止方位
	 */
	public ParameterParser(

			 boolean isColocWise,
			 int start_dist,
			 int start_elev,
			 int start_azi,
			 int end_dist,
			 int end_elev,
			 int end_azi){

		this.isColocWise = isColocWise;
		this.start_dist = start_dist;
		this.start_elev = start_elev;
		this.start_azi = start_azi;
		this.end_dist = end_dist;
		this.end_elev = end_elev;
		this.end_azi = end_azi;
		
		divideParam();
	}
	
	
	/**
	 * 将起点和终点的坐标进行分割，在两个坐标点之间填充合适的值，构成3个参数数组
	 * 三个数组做好之后长度并不一样长，需要进一步进行匹配，才能获得3个等长的运动轨迹数组
	 */
	private void divideParam(){
		
		//----------------------------------------
		dists = new ArrayList<Integer>();
		
		dists.add(start_dist);//将距离起点放入
        int dis_inc = 0;
        if (start_dist < end_dist) dis_inc = 5;
        if (start_dist > end_dist) dis_inc = -5;
        int dis_start = start_dist;
        while (true)
        {
            if (dis_inc == 0) break;
            dis_start += dis_inc;
            if (dis_start != 20 &&
                dis_start != 30 &&
                dis_start != 40 &&
                dis_start != 50 &&
                dis_start != 75 &&
                dis_start != 100 &&
                dis_start != 130 &&
                dis_start != 160
                ) continue;
            dists.add(dis_start);
            if (dis_start == end_dist) break;
        }
        //---------------------------------------
        int incresment = 5;
        azis = new ArrayList<Integer>();
        azis.add(start_azi);//先将起点放进去

        if ( start_azi != end_azi)
        {
            if (!isColocWise)//逆时针
            {
                if (start_azi > end_azi)
                {
                    incresment = -5;
                    for (int i = start_azi + incresment; i >= end_azi; i += incresment)
                    {
                        azis.add(i);
                    }
                }
                else if (start_azi < end_azi)
                {
                    incresment = -5;
                    for (int i = start_azi + incresment; i >= 0; i += incresment)
                    {
                        azis.add(i);
                    }
                    for (int i = 360 + incresment; i >= end_azi; i += incresment)
                    {
                        azis.add(i);
                    }
                }

            }
            else//顺时针
            {
                if (end_azi > start_azi)
                {
                    incresment = 5;
                    for (int i = start_azi + incresment; i <= end_azi; i += incresment)
                    {
                        azis.add(i);
                    }
                }
                else if (end_azi < start_azi)
                {
                    incresment = 5;
                    for (int i = start_azi + incresment; i <= 360; i += incresment)
                    {
                        azis.add(i);
                    }
                    for (int i = 0 + incresment; i <= end_azi; i += incresment)
                    {
                        azis.add(i);
                    }
                }
            }
                       
        }//------:> 
        //-------------------------------------------
		
        elevs = new ArrayList<Integer>();
        elevs.add(start_elev);//将起点高度放入
        int elev_inc = 0;
        if (start_elev < end_elev) elev_inc = 10;
        if (start_elev > end_elev) elev_inc = -10;
        int elev_start = start_elev;
        while (true)
        {
            if (elev_inc == 0) break;
            elev_start += elev_inc;
            elevs.add(elev_start);
            if (elev_start == end_elev) break;
        }
        //-------------------------------------------
        
        elev_azi_match(azis, elevs, dists);
        
        
        
        
	}
	
	
	/**
	 * 根据传入的三个参数数组，选择最长的数组长度，将其他两个适配到同样的长度，并进行分散，使得各个数组长度一致，并都能对应上HRTF函数
	 * 计算结果依然由传入参数返回
	 * @param azis_
	 * @param eles_
	 * @param diss_
	 */
	private void elev_azi_match(ArrayList<Integer> azis_, ArrayList<Integer> eles_, ArrayList<Integer> diss_)
    {
        int azi_count = azis_.size();
        int ele_count = eles_.size();
        int dis_count = diss_.size();

        //----- 判定出三个参数谁的数据量最大  ，这个跟dist没多大关系？……  还没有考虑清楚——erhu
        int max = 0;

        
        if (azi_count >= ele_count && azi_count >= dis_count) max = azi_count;
        else if (ele_count >= azi_count && ele_count >= dis_count) max = ele_count;
        else if (dis_count >= azi_count && dis_count >= ele_count) max = dis_count;

        int[] ele_2 = new int[max];
        int[] azi_2 = new int[max];
        int[] dis_2 = new int[max];

        {
       //---- 对距离的不精确规划
            double dis_scale = (double)(diss_.get(diss_.size() - 1) - diss_.get(0)) / (double)max;

            for (int i = 0; i < max; i++)
            {
                double diss_double = diss_.get(0) + dis_scale * i;
                if (diss_double < 25) dis_2[i] = 20;
                else if (diss_double >= 25 && diss_double < 35) dis_2[i] = 30;
                else if (diss_double >= 35 && diss_double < 45) dis_2[i] = 40;
                else if (diss_double >= 45 && diss_double < 62.5) dis_2[i] = 50;
                else if (diss_double >= 62.5 && diss_double < 87.5) dis_2[i] = 75;
                else if (diss_double >= 87.5 && diss_double < 115) dis_2[i] = 100;
                else if (diss_double >= 115 && diss_double < 145) dis_2[i] = 130;
                else dis_2[i] = 160;
            }
            dis_2[max-1] = diss_.get(dis_count-1);
            
            {//----找出最大维数 建立关系 ele
	
            	int scal = eles_.get(eles_.size()-1) - eles_.get(0);
                double bili = (double)scal / (double)max;

                for (int i = 0; i < max; i++)
                {
                    ele_2[i] = (int)Math.round(eles_.get(0) + bili * i);
                }

                for (int i = 0; i < max; i++)
                {
                    int min_ele_num = 999;
                    int min_ele_index = 0;
                    for (int j = 0; j < ele_count; j++)
                    {

                        int min = Math.abs(ele_2[i] - eles_.get(j));
                        if (min < min_ele_num)
                        {
                            min_ele_index = j;
                            min_ele_num = min;
                        }
                    }
                    ele_2[i] = eles_.get(min_ele_index);
                }
                ele_2[max-1] = eles_.get(ele_count-1);
            }
            {//----   azi
            
            	int degres = 0;
                if (!isColocWise)//逆时针
                {
                    if (azis_.get(azi_count-1) > azis_.get(0))
                        degres = (azis_.get(0) - 0) + (360 - azis_.get(azi_count-1));
                    else
                        degres = azis_.get(0) - azis_.get(azi_count-1);

                }
                else//顺时针
                {
                    if (azis_.get(azi_count-1)> azis_.get(0))
                        degres =  azis_.get(azi_count-1) - azis_.get(0);
                    else
                        degres = (360- azis_.get(0) ) + azis_.get(azi_count-1);
                }

                degres = Math.abs(degres);

                double bili = (double)degres / (double)max;
                if (!isColocWise)
                    bili *= -1;

                for (int i = 0; i < max; i++)
                {
                    azi_2[i] = (int)Math.round(azis_.get(0) + bili * i );
                    if (azi_2[i] > 360) azi_2[i] -= 360;
                    if (azi_2[i] < 0) azi_2[i] += 360;
                }

                for (int i = 0; i < max; i++)
                {
                    int min_azi_num = 999;
                    int min_azi_index = 0;
                    for (int j = 0; j < azi_count; j++)
                    {

                        int min = Math.abs(azi_2[i] - azis_.get(j));
                        if (min < min_azi_num)
                        {
                            min_azi_index = j;
                            min_azi_num = min;
                        }
                    }
                    azi_2[i] = azis_.get(min_azi_index);
                }
            }//--->azi处理结束
        }

        //------------->对ele进行分类处理
        int index;
        int min_ = 999;
        for (int i = 0; i < max; i++)
        {
            switch (ele_2[i])
            {
                case 90:
                    if (azi_2[i] < 180) azi_2[i] = 0;
                    else azi_2[i] = 360;
                    break;
                case 80:
                    min_ = 999;
                    index = azi_2[i];
                    for (int j = 0; j <= 360; j += 30)
                    {
                        if (Math.abs(azi_2[i] - j) < min_)
                        {
                            index = j;
                            min_ = Math.abs(azi_2[i] - j);
                        }
                    }
                    azi_2[i] = index;
                    break;
                case 70:
                    min_ = 999;
                    index = azi_2[i];
                    for (int j = 0; j <= 360; j += 15)
                    {
                        if (Math.abs(azi_2[i] - j) < min_)
                        {
                            index = j;
                            min_ = Math.abs(azi_2[i] - j);
                        }
                    }
                    azi_2[i] = index;
                    break;
                case 60:
                    min_ = 999;
                    index = azi_2[i];
                    for (int j = 0; j <= 360; j += 10)
                    {
                        if (Math.abs(azi_2[i] - j) < min_)
                        {
                            index = j;
                            min_ = Math.abs(azi_2[i] - j);
                        }
                    }
                    azi_2[i] = index;
                    break;
                default:
                    break;
            }
        }
        //------------->对ele进行分类处理over
      

        azis_.clear();
        eles_.clear();
        diss_.clear();

//
//        StringBuilder sb = new StringBuilder();
//
        for (int i = 0; i < max; i++)
        {
            azis_.add(azi_2[i]);
            eles_.add(ele_2[i]);
            diss_.add(dis_2[i]);
//            sb.append(azi_2[i]).append(",").append(ele_2[i]).append(",").append(dis_2[i]).append("\n");

        }
//        System.out.println(sb);

      


    }
	
}
