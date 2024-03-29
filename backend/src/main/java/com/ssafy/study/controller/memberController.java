package com.ssafy.study.controller;




import com.ssafy.study.dto.memberDTO;
import com.ssafy.study.dto.passwordDTO;
// import org.springframework.web.bind.annotation.RestController;
import com.ssafy.study.dto.updateMemberNoImageDTO;
import com.ssafy.study.util.MailSender;
import com.ssafy.study.util.MakePassword;
import com.ssafy.study.model.BasicResponse;
import com.ssafy.study.model.DateForUser;
import com.ssafy.study.model.Feed;
import com.ssafy.study.model.Follow;
import com.ssafy.study.model.Member;
import com.ssafy.study.model.MyLicense;
import com.ssafy.study.model.Studyroom;
import com.ssafy.study.model.StudyroomUser;
import com.ssafy.study.repository.CommentRepository;
import com.ssafy.study.repository.DateForStudyroomRepository;
import com.ssafy.study.repository.DateForUserRepository;
import com.ssafy.study.repository.FeedRepository;
import com.ssafy.study.repository.FollowRepository;
import com.ssafy.study.repository.HashtagRepository;
import com.ssafy.study.repository.LicenseRepository;
import com.ssafy.study.repository.LicenseReviewRepository;
import com.ssafy.study.repository.LikeRepository;
import com.ssafy.study.repository.MemberRepository;
import com.ssafy.study.repository.MyLicenseRepository;
import com.ssafy.study.repository.NotificationRepository;
import com.ssafy.study.repository.ReqEntityRepository;
import com.ssafy.study.repository.StudyroomRepository;
import com.ssafy.study.repository.StudyroomUserRepository;
import com.ssafy.study.util.MailSender;
import com.ssafy.study.util.MakePassword;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@ApiResponses(value = { @ApiResponse(code = 401, message = "Unauthorized", response = BasicResponse.class),
        @ApiResponse(code = 403, message = "Forbidden", response = BasicResponse.class),
        @ApiResponse(code = 404, message = "Not Found", response = BasicResponse.class),
        @ApiResponse(code = 500, message = "Failure", response = BasicResponse.class) })


@CrossOrigin(origins = { "http://i3a102.p.ssafy.io" })
@RestController("/member")
//@RequestMapping("/member")
public class memberController {

    @Autowired
    MemberRepository memberRepo;
    
    @Autowired
    FollowRepository followRepo;

    @Autowired
    DateForUserRepository dateforuserRepo;

    @Autowired
    LicenseRepository licenseRepo;

    @Autowired
    MailSender mailSender;
    
    @Autowired
    StudyroomRepository studyroomRepo;
    
    @Autowired
    StudyroomUserRepository studyroomuserRepo;
    
    @Autowired
    FeedRepository feedRepo;
    
    @Autowired
    CommentRepository commentRepo;
    
    @Autowired
    MyLicenseRepository mylicenseRepo;
    
    @Autowired
    LicenseReviewRepository licensereviewRepo;
    
    @Autowired
    LikeRepository likeRepo;
    
    @Autowired
    DateForStudyroomRepository dateforstudyroomRepo;
    
    @Autowired
    HashtagRepository hashRepo;
    
    @Autowired
    NotificationRepository notiRepo;
    
    @Autowired
    ReqEntityRepository reqRepo;


    @PostMapping("/join")
    public Object addNewMember(@RequestBody Member member, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        
        System.out.println("join() : "+member.getUserEmail()+","+member.getUserName()+","+member.getPassword());
        if(member.getUserEmail()==null) {
        	result.status = true;
        	result.data = "이메일 null.";
        	return new ResponseEntity<>(result, HttpStatus.OK);
        }
        Optional<Member> checkmember = memberRepo.findByUserEmail(member.getUserEmail());
        if(checkmember.isPresent()) {
        	result.status = false;
        	result.data = "exist";
        	return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        
        memberRepo.save(member);
        result.status=true;
        result.data="success";

        response=new ResponseEntity<>(result, HttpStatus.OK);


        return response;
    }
    
    @PostMapping("checkemail")
    public Object checkEmail(@RequestBody Member member, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();

        Optional<Member> checkmember = memberRepo.findByUserEmail(member.getUserEmail());
        if(checkmember.isPresent()) {
            result.status = false;
            result.data = "exist";
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        MakePassword makePassword = new MakePassword();

        String token = makePassword.getRamdomPassword(5);

        mailSender.sendCheck(member.getUserEmail(),token);
        result.status=true;
        result.data="success";
        result.object=token;
        response=new ResponseEntity<>(result, HttpStatus.OK);


        return response;
    }

    @PostMapping("/updateMyInfo")
    public Object updateMyInfo(@RequestBody Member member, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();

        Optional<Member> checkmember = memberRepo.findById(member.getId());
        if(!checkmember.isPresent()) {
            result.status = false;
            result.data = "잘못된 계정.";
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        
        memberRepo.save(member);
        result.status=true;
        result.data="success";

        response=new ResponseEntity<>(result, HttpStatus.OK);

        return response;
    }
    
    @PostMapping("/updateMyInfoNoImage")
    public Object updateMyInfoNoImage(@ModelAttribute updateMemberNoImageDTO memberDTO) {
    	ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        
        Optional<Member> member = memberRepo.findById(memberDTO.getId());
        if(!member.isPresent()) {
        	result.status=false;
        	result.data="멤버를 찾을 수 없음.";
        	return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        
        member.get().setUserName(memberDTO.getUserName());
        member.get().setUserContent(memberDTO.getUserContent());
        member.get().setMajor(memberDTO.getMajor());
        member.get().setMajorSeq(memberDTO.getMajorSeq());
        member.get().setEducation(memberDTO.getEducation());
        member.get().setField1(memberDTO.getField1());
        member.get().setDesiredField1(memberDTO.getDesiredField1());
        member.get().setDesiredField2(memberDTO.getDesiredField2());
        member.get().setDesiredField3(memberDTO.getDesiredField3());
        member.get().setSecret(memberDTO.isSecret());
        
        memberRepo.save(member.get());
        
        result.status=true;
        result.data="success";
        
        response=new ResponseEntity<>(result, HttpStatus.OK);

        return response;
    }
    
    
    @PostMapping("/updateMyInfoWithImage")
    public Object updateMyInfo2(@ModelAttribute memberDTO memberDTO) throws IOException {
    	 ResponseEntity response = null;
         BasicResponse result = new BasicResponse();

         Optional<Member> member = memberRepo.findById(memberDTO.getId());
         if(!member.isPresent()) {
        	 result.status=false;
             result.data="멤버를 찾을 수 없음.";
             return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
         }
         
         member.get().setUserName(memberDTO.getUserName());
         member.get().setUserContent(memberDTO.getUserContent());
         member.get().setUserThumbnail(memberDTO.getUserThumbnail().getBytes());
         member.get().setImageType(memberDTO.getUserThumbnail().getContentType());
         member.get().setMajor(memberDTO.getMajor());
         member.get().setMajorSeq(memberDTO.getMajorSeq());
         member.get().setEducation(memberDTO.getEducation());
         member.get().setField1(memberDTO.getField1());
         member.get().setDesiredField1(memberDTO.getDesiredField1());
         member.get().setDesiredField2(memberDTO.getDesiredField2());
         member.get().setDesiredField3(memberDTO.getDesiredField3());
         member.get().setSecret(memberDTO.isSecret());
         
         memberRepo.save(member.get());
         
         result.status=true;
         result.data="success";
         
         response=new ResponseEntity<>(result, HttpStatus.OK);

         return response;
    }
    
    @PostMapping("/getImage")
    public Object getImage(MultipartFile userThumbnail) throws IOException {
   	 	ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        System.out.println(userThumbnail.isEmpty());
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("thumbnail",userThumbnail.getBytes());
        map.put("thumbnailType",userThumbnail.getContentType());
        
        result.status=true;
        result.data="success";
        result.object=map;
        
        response=new ResponseEntity<>(result, HttpStatus.OK);

        return response;
   }
    
    @PostMapping("/changePassword")
    public Object changePassword(@RequestBody passwordDTO password, HttpSession session) {
    	ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        
        Optional<Member> member = memberRepo.findByIdAndPassword(password.getUID(), password.getCurrentPassword());
        if(!member.isPresent()) {
        	result.status=false;
        	result.data="현재 비밀번호를 확인해주세요!";
        	result.object=false;
        	return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        if(password.getCurrentPassword().equals(password.getNewPassword())) {
        	result.status=false;
        	result.data="동일한 비밀번호입니다!";
        	result.object=false;
        	return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
        }
        	
        member.get().setPassword(password.getNewPassword());
        memberRepo.save(member.get());
        
        result.status=true;
        result.data="success";
        result.object=true;
        response=new ResponseEntity<>(result, HttpStatus.OK);
        
        return response;
    }
    
    @Transactional
    @PostMapping("/withdrawal")
    public Object withdrawal(@RequestBody Member member, HttpSession session) {
    	ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        
        Optional<Member> checkmember = memberRepo.findById(member.getId());
        if(!checkmember.isPresent()) {
        	result.status = false;
        	result.data = "해당 멤버를 찾을 수 없음";
        	return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        
        // 방장인 경우
        Iterator<Studyroom> iter = studyroomRepo.findAllByCaptainId(member.getId()).stream().collect(Collectors.toSet()).iterator();
        while(iter.hasNext()) { // 방장인 모든 스터디룸
        	Studyroom room = iter.next();
        	for (StudyroomUser roomuser : studyroomuserRepo.findAllByStudyroom(room)) {
        		for (DateForUser date : dateforuserRepo.findAllByMember(roomuser.getMember())) {
					if(date.getDateForStudyroom().getStudyroom().equals(room))
						dateforuserRepo.deleteById(date.getId());
				}
        	}
        	dateforstudyroomRepo.deleteAllByStudyroom(room);
        	hashRepo.deleteAllByStudyroom(room);
        	for (Feed feed : feedRepo.findAllByStudyroom(room)) {
				likeRepo.deleteAllByFeed(feed);
				commentRepo.deleteAllByFeed(feed);
			}
        	feedRepo.deleteAllByStudyroom(room);
        	studyroomuserRepo.deleteAllByStudyroom(room);
        	studyroomRepo.deleteById(room.getId());
        }
        
        // 방장이 아닌 경우
        studyroomuserRepo.deleteAllByMember(checkmember.get());
        followRepo.deleteAllByFrom(checkmember.get());
        followRepo.deleteAllByTarget(checkmember.get());
        dateforuserRepo.deleteAllByMember(checkmember.get());
        likeRepo.deleteAllByMember(checkmember.get());
        commentRepo.deleteAllByMember(checkmember.get());
        feedRepo.deleteAllByMember(checkmember.get());
        notiRepo.deleteAllByFromMember(checkmember.get());
        notiRepo.deleteAllByToMember(checkmember.get());
        reqRepo.deleteAllByFromMember(checkmember.get());
        reqRepo.deleteAllByToMember(checkmember.get());
        mylicenseRepo.deleteAllByMember(checkmember.get());
        licensereviewRepo.deleteAllByReviewWriter(checkmember.get());
        memberRepo.deleteById(member.getId());
        
        result.status=true;
        result.data="success";
        
        response=new ResponseEntity<>(result, HttpStatus.OK);
        
        return response;
    }
    
    @PostMapping("/findpassword")
    public Object findPassword(@RequestBody Member member, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();

        Optional<Member> checkmember = memberRepo.findByUserEmail(member.getUserEmail());
        if(!checkmember.isPresent()) {
            result.status = false;
            result.data = "해당 email을 찾을 수 없음";
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        MakePassword makePassword = new MakePassword();

        String password = makePassword.getRamdomPassword(10);
        checkmember.get().setPassword(password);
        mailSender.sendMail(checkmember.get().getUserEmail(),password);
        memberRepo.save(checkmember.get());
        result.status=true;
        result.data="success";

        response=new ResponseEntity<>(result, HttpStatus.OK);


        return response;
    }


    @PostMapping("/getUser")
    public Object getUser(@RequestBody Map<String, String> map, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        System.out.println(map.get("id"));
        Long uid = Long.parseLong(map.get("id"));
        


        Optional<Member> checkmember = memberRepo.findById(uid);
        if(!checkmember.isPresent()) {
            result.status = false;
            result.data = "잘못된 계정.";
            return new ResponseEntity<>(result, HttpStatus.CONFLICT);
        }
        
        
        result.status=true;
        result.data="success";
        result.object=checkmember.get();
        
        response=new ResponseEntity<>(result, HttpStatus.OK);


        return response;
    }
    
    @GetMapping("/login")
    public Object Login() {
    	return "hi";
    }

    @PostMapping("/login")
    public Object Login(@RequestBody Member loginMember, HttpSession session) {
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        String userEmail = loginMember.getUserEmail();
        String password = loginMember.getPassword();
        System.out.println("Login call() : "+userEmail+","+password);
        Optional<Member> member = memberRepo.findByUserEmailAndPassword(userEmail, password);
        if(!member.isPresent()) {
        	result.status = false;
        	result.data = "ID가 없거나 틀린 비밀번호가 입력 됨";
        	return new ResponseEntity<>(result, HttpStatus.NOT_FOUND);
        }

        /////////////////////////////
        session.setAttribute("uid",member.get().getId());
        
        /////////////////////////////
        result.status=true;
        //Map<String,Long> token=new HashMap<>();
        //token.put("auth-token",member.get().getId()*3449447);
        result.data="success";
        result.object=member.get().getId();

        response=new ResponseEntity<>(result, HttpStatus.OK);


        return response;
    }
    
    @PostMapping("/logout")
    public Object Logout(HttpSession session) {
    	ResponseEntity response = null;
    	BasicResponse result = new BasicResponse();
    	
    	////////////////////
    	// session
    	session.invalidate();
    	////////////////////
    	result.status = true;
    	result.data = "success";
    	
    	response = new ResponseEntity<>(result, HttpStatus.OK);
    	
    	return response;
    }

    
    @PostMapping("/follow")
    public Object follow(@RequestBody Map<String,String> map, HttpSession session) {
    	ResponseEntity response = null;
    	BasicResponse result = new BasicResponse();
    	System.out.println(map.get("uid")+","+map.get("targetid"));
    	Long id = Long.parseLong(map.get("uid"));
    	Long targetUID = Long.parseLong(map.get("targetid"));
        Optional<Member> member = memberRepo.findById(id);
        Optional<Member> targetMember = memberRepo.findById(targetUID);
        if(!member.isPresent()||!targetMember.isPresent()){
            result.status=false;
            result.data="멤버를 찾을 수 없음.";
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        Follow follow = member.get().follow(targetMember.get());
        if(followRepo.findByFromAndTarget(member.get(), targetMember.get()).isPresent()) {
        	 result.status=false;
             result.data="이미 팔로우 된 상태";
             return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        followRepo.save(follow);
        result.status=true;
        result.data="success";
        response= new ResponseEntity<>(result,HttpStatus.OK);
        return response;
    }
    
    @PostMapping("/followstate")
    public Object followstate(@RequestBody Map<String,String> map, HttpSession session) {
    	ResponseEntity response = null;
    	BasicResponse result = new BasicResponse();
    	System.out.println(map.get("uid")+","+map.get("targetid"));
    	Long id = Long.parseLong(map.get("uid"));
    	Long targetUID = Long.parseLong(map.get("targetid"));
        Optional<Member> member = memberRepo.findById(id);
        Optional<Member> targetMember = memberRepo.findById(targetUID);
        if(!member.isPresent()||!targetMember.isPresent()){
            result.status=false;
            result.data="멤버를 찾을 수 없음.";
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        if(followRepo.findByFromAndTarget(member.get(), targetMember.get()).isPresent()) {
        	result.object=true;
        }else {
        	result.object=false;
        }
        result.status=true;
        result.data="success";
        response= new ResponseEntity<>(result,HttpStatus.OK);
        return response;
    }
    
    @PostMapping("/unfollow")
    public Object cancelFollow(@RequestBody Map<String,String> map, HttpSession session) {
    	ResponseEntity response = null;
    	BasicResponse result = new BasicResponse();
    	System.out.println(map.get("uid")+","+map.get("targetid"));
    	Long id = Long.parseLong(map.get("uid"));
    	Long targetUID = Long.parseLong(map.get("targetid"));
        Optional<Member> member = memberRepo.findById(id);
        Optional<Member> targetMember = memberRepo.findById(targetUID);
        if(!member.isPresent()||!targetMember.isPresent()){
            result.status=false;
            result.data="멤버를 찾을 수 없음.";
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }

        followRepo.findByFromAndTarget(member.get(), targetMember.get()).ifPresent(followRepo::delete);;
        result.status=true;
        result.data="success";
        response= new ResponseEntity<>(result,HttpStatus.OK);
        return response;
    }
    

    @PostMapping("/getFollower")
    public Object getFollower(@RequestBody Map<String,String> map, HttpSession session){
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        Long targetUID = Long.parseLong(map.get("targetid"));
        Optional<Member> targetMember = memberRepo.findById(targetUID);
        if(!targetMember.isPresent()){
            result.status=false;
            result.data="멤버를 찾을 수 없음.";
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        
        result.status=true;
        result.data="success";
       
        result.object=followRepo.findAllByTargetEquals(targetMember.get()).stream().map(Follow::getFrom).collect(Collectors.toSet());
        response= new ResponseEntity<>(result,HttpStatus.OK);

        return response;
    }

    @PostMapping("/getFollowing")
    public Object getFollowing(@RequestBody Map<String,String> map, HttpSession session){
        ResponseEntity response = null;
        BasicResponse result = new BasicResponse();
        Long targetUID = Long.parseLong(map.get("targetid"));
        Optional<Member> targetMember = memberRepo.findById(targetUID);
        if(!targetMember.isPresent()){
            result.status=false;
            result.data="멤버를 찾을 수 없음.";
            return new ResponseEntity<>(result, HttpStatus.FORBIDDEN);
        }
        result.status=true;
        result.data="success";
        result.object=followRepo.findAllByFromEquals(targetMember.get()).stream().map(Follow::getTarget).collect(Collectors.toSet());
        response= new ResponseEntity<>(result,HttpStatus.OK);

        return response;
    }
    
}