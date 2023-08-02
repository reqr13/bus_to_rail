package com.example.korail.controller;

import com.example.korail.dto.CardinfoDto;
import com.example.korail.dto.OrderDto;
import com.example.korail.dto.ReservationDto;
import com.example.korail.service.CardinfoService;
import com.example.korail.service.MileageService;
import com.example.korail.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.UUID;

@Controller
public class ReservationController {
@Autowired
    OrderService orderService;
@Autowired
    CardinfoService cardinfoService;

    @Autowired
    MileageService mileageService;

    //결제 완료 페이지
    @PostMapping("train_reservation_pymcfm")
    public String train_reservation_pymcfm(ReservationDto reservationDto,HttpSession session, OrderDto orderDto, CardinfoDto cardinfoDto,Model model){
        ReservationDto rvo = (ReservationDto)session.getAttribute("rvo");
        UUID uuid = UUID.randomUUID();
        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        int number = Integer.parseInt(rvo.getAdltTotAmt());
        String price = decimalFormat.format(number);

        rvo.setEmail(reservationDto.getEmail());

        if(cardinfoDto.getPaymentmethod().equals("card")) {
            cardinfoDto.setRecognizenum(uuid.toString().replaceAll("-", "").substring(0, 10));
            cardinfoService.getPayment(cardinfoDto);
        }else if(cardinfoDto.getPaymentmethod().equals("kakao")){
            cardinfoService.getPayment(cardinfoDto);
        }

        orderDto.setSstation(rvo.getDepplacename());
        orderDto.setStime(rvo.getStart_date());
        orderDto.setDtime(rvo.getEnd_date());
        orderDto.setDstation(rvo.getArrplacename());
        orderDto.setReservnum(uuid.toString().substring(0, 8));  //예매 번호
        orderDto.setChairnum(rvo.getSeatNum());
        orderDto.setId(rvo.getId());
        orderDto.setDepPlaceId(rvo.getStartId());
        orderDto.setArrPlaceId(rvo.getEndId());
        orderDto.setDepPlandTime(rvo.getRtimes());
        orderDto.setCardnum(cardinfoDto.getCardnum());
        orderDto.setPrice(price);
        orderDto.setTrainnum(Integer.parseInt(rvo.getTrainno()));
        orderDto.setTicketqty(Integer.parseInt(rvo.getTicketQty()));
        orderDto.setEmail(rvo.getEmail());

        orderService.getPayment(orderDto);

        mileageService.setMileage(rvo.getId(), "열차 예매", Integer.parseInt(orderDto.getPrice()));

        return "reservation/train_reservation_pymcfm";
    }

    //회원 결제 페이지
    @GetMapping("train_reservation_stplcfmpym")
    public String train_reservation_stplcfmpym(HttpSession session){
        return "reservation/train_reservation_stplcfmpym";
    }

    //비회원 결제 페이지
    @PostMapping("train_reservation_stplcfmpym2")
    public String train_reservation_stplcfmpym2(ReservationDto reservationDto, HttpSession session){
        ReservationDto rvo = (ReservationDto)session.getAttribute("rvo");

        rvo.setSeatNum(reservationDto.getSeatNum2());
        rvo.setTicketQty(reservationDto.getTicketQty2());
        rvo.setAdltTotAmt(reservationDto.getAdltTotAmt2());
        rvo.setId("GUEST");

        return "reservation/train_reservation_stplcfmpym";
    }

    //로그인 한 상황
    @GetMapping("stplcfmpym")
    public String stplcfmpym(HttpSession session,String seatNum, String ticketQty, String id, String email,String adltTotAmt){
        ReservationDto rvo = (ReservationDto)session.getAttribute("rvo");

        rvo.setSeatNum(seatNum);
        rvo.setTicketQty(ticketQty);
        rvo.setId(id);
        rvo.setEmail(email);
        rvo.setAdltTotAmt(adltTotAmt);
        //System.out.println("로그인 한 경우 -->"+rvo.getEmail());
        //System.out.println("로그인 한 경우-->"+rvo.getAdltTotAmt());
        return "/reservation/train_reservation_stplcfmpym";
    }

    //예매 좌석 선택 페이지
    @PostMapping("train_reservation_satschc")
    public String train_reservation_satschc(ReservationDto rvo, HttpSession session
            ,String depplacename, String arrplacename, String start_date, String end_date, String traingradename, String trainno, String adultcharge, String rtimes){

        rvo = (ReservationDto)session.getAttribute("rvo");
        rvo.setDepplacename(depplacename);
        rvo.setArrplacename(arrplacename);
        rvo.setStart_date(start_date);
        rvo.setEnd_date(end_date);
        rvo.setTraingradename(traingradename);
        rvo.setTrainno(trainno);
        rvo.setAdultcharge(adultcharge);
        rvo.setRtimes(rtimes);

        return "reservation/train_reservation_satschc";
    }

    //시간 선택 페이지
    @GetMapping("train_reservation_alcnsrch/{startId}/{endId}/{rtime}/{start_add}/{end_add}")
    public String train_reservation_alcnsech(ReservationDto rvo, HttpSession session,@PathVariable String startId,@PathVariable String endId,@PathVariable String rtime,@PathVariable String start_add,@PathVariable String end_add){

        rvo.setStartId(startId);
        rvo.setEndId(endId);
        rvo.setRtime(rtime);
        rvo.setStart_add(start_add);
        rvo.setEnd_add(end_add);

        session.setAttribute("rvo", rvo);

        return "reservation/train_reservation_alcnsrch";
    }
    //예매 메인 페이지
    @GetMapping("train_reservation_rotinf")
    public String train_reservation_rotinf(){
        return "reservation/train_reservation_rotinf";
    }
    //메인 페이지

}
