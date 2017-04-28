package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;

import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignTeacherDetailsActivity extends AppCompatActivity {
    private SignType signType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_teacher_details);
    }
    private void getStudentOfSign(){//的到签到对应的所有学生
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
}
