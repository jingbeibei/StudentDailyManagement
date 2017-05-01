package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class SignTeacherDetailsActivity extends AppCompatActivity {
    private SignType signType;
    private String s="select * from signRecord where typeid=xxx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_teacher_details);
    }
    private void getStudentOfSign(){//得到签到对应的所有学生
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.addWhereRelatedTo("visibleClass", new BmobPointer(signType));
        query.findObjects(new FindListener<StudentClass>() {
            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if(e==null){
                    String[] classArray= new String[object.size()];
                    int i=0;
                    for(StudentClass studentClass:object){
                        classArray[i]=studentClass.getObjectId();
                    }
                    getStudentOfClass(classArray);
                    Log.i("bmob","查询个数："+object.size());
                }else{
                    Log.i("bmob","失败："+e.getMessage());
                }
            }

        });
    }

    private void getStudentOfClass(String[] studentClasss){//可以得到涉及的所有学生数
        BmobQuery<Student> query = new BmobQuery<Student>();
        query. addWhereContainsAll("studentClass", Arrays.asList(studentClasss));
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if(e==null){
                    if (list.size()!=0){

                    }
                }
            }
        });
    }
    private void getStudentOfSignSuccess(){//查询本次签到成功的所有记录
        BmobQuery<SignRecord> query=new BmobQuery<>();
        query.addWhereEqualTo("signType",new BmobPointer(signType));
        query.addWhereEqualTo("isSuccess" ,1);
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {

            }
        });
    }

    private void getStudentOfSignFail(){//查询本次签到失败的所有记录
        BmobQuery<SignRecord> query=new BmobQuery<>();
        query.addWhereEqualTo("signType",new BmobPointer(signType));
        query.addWhereEqualTo("isSuccess" ,1);
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {

            }
        });
    }
    private void getStudentOfUnsign(){//查询未签到学生
        String bql ="select * from Student where ObjectId not in (select include student.ObjectId from SignRecord where related signType to pointer('signType', '"+signType.getObjectId()+"') )";
        new BmobQuery<Student>().doSQLQuery(bql,new SQLQueryListener<Student>(){

            @Override
            public void done(BmobQueryResult<Student> result, BmobException e) {
                if(e ==null){
                    List<Student> list = (List<Student>) result.getResults();
                    if(list!=null && list.size()>0){

                    }else{
                        Log.i("smile", "查询成功，无数据返回");
                    }
                }else{
                    Log.i("smile", "错误码："+e.getErrorCode()+"，错误描述："+e.getMessage());
                }
            }
        });
    }

}
