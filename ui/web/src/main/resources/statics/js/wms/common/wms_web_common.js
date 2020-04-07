function getLastMonthYestdy(date){
    var date = new Date(); //        1    2    3    4    5    6    7    8    9   10    11   12月
    var daysInMonth = new Array([0],[31],[28],[31],[30],[31],[30],[31],[31],[30],[31],[30],[31]);
    var strYear = date.getFullYear();
    var strDay = date.getDate();
    var strMonth = date.getMonth()+1;
    if(strYear%4 == 0 && strYear%100 != 0){//一、解决闰年平年的二月份天数   //平年28天、闰年29天//能被4整除且不能被100整除的为闰年
        daysInMonth[2] = 29;
    }
    if(strMonth - 1 == 0) //二、解决跨年问题
    {
        strYear -= 1;
        strMonth = 12;
    }
    else
    {
        strMonth -= 1;
    }
//    strDay = daysInMonth[strMonth] >= strDay ? strDay : daysInMonth[strMonth];
    strDay = Math.min(strDay,daysInMonth[strMonth]);//三、前一个月日期不一定和今天同一号，例如3.31的前一个月日期是2.28；9.30前一个月日期是8.30

    if(strMonth<10)//给个位数的月、日补零
    {
        strMonth="0"+strMonth;
    }
    if(strDay<10)
    {
        strDay="0"+strDay;
    }
    datastr = strYear+"-"+strMonth+"-"+strDay;
    return datastr;

}

/**
 * input框扫描录入
 * @param e
 * @returns
 */
var keydownFirstTime = 0;
function scanInput(e){
	var date = new Date(); 
    var nowTime = date.getTime();
    if (e.value.length<2) {
          keydownFirstTime = nowTime;          
      }
    console.info( e.value.length)
    if(nowTime-keydownFirstTime >200&&e.value.length<5){
    	   e.value='';
       }
}
