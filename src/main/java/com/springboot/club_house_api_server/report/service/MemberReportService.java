package com.springboot.club_house_api_server.report.service;

import com.springboot.club_house_api_server.club.entity.ClubEntity;
import com.springboot.club_house_api_server.club.repository.ClubRepository;
import com.springboot.club_house_api_server.event.repository.ClubEventRepository;
import com.springboot.club_house_api_server.game.repository.GameParticipantRepository;
import com.springboot.club_house_api_server.guest.repository.GuestRepository;
import com.springboot.club_house_api_server.membership.repository.MembershipRepository;
import com.springboot.club_house_api_server.openai.analyze.service.OpenAIService;
import com.springboot.club_house_api_server.participant.dto.MembersReportDto;
import com.springboot.club_house_api_server.participant.repository.ParticipantRepository;
import com.springboot.club_house_api_server.report.dto.MemberChartDataDto;
import com.springboot.club_house_api_server.user.dto.UserInfoDto;
import com.springboot.club_house_api_server.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MemberReportService {
    private final MembershipRepository membershipRepository;
    private final GuestRepository guestRepository;
    private final ClubRepository clubRepository;
    private final ParticipantRepository participantRepository;
    private final ClubEventRepository clubEventRepository;
    private final GameParticipantRepository gameParticipantRepository;
    private final OpenAIService openAIService;


    public ResponseEntity<?> getMemberReportChartData(Long clubId, int year, int month) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }
        ClubEntity club = clubOpt.get();



        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(23, 59, 59);
        List<UserEntity> guestUserIds = guestRepository.findAttendedGuestUserIds(startDate, endDate);
        List<MembersReportDto> attendanceCounts = participantRepository.findMembershipAttendanceCount(startDate, endDate);
        UserEntity topAttendant = participantRepository.findTopEventParticipant(PageRequest.of(0, 1)).get(0);
        UserEntity topGamer = gameParticipantRepository.findTopGameParticipant(PageRequest.of(0, 1)).get(0);
        UserInfoDto topAttendantUserInfo = UserInfoDto.builder()
                .userName(topAttendant.getUserName())
                .userTel(topAttendant.getUserTel())
                .gender(topAttendant.getGender())
                .region(topAttendant.getRegion())
                .career(topAttendant.getCareer())
                .birthDate(topAttendant.getBirthDate())
                .build();

        UserInfoDto topGamerUserInfo = UserInfoDto.builder()
                .userName(topGamer.getUserName())
                .userTel(topGamer.getUserTel())
                .gender(topGamer.getGender())
                .region(topGamer.getRegion())
                .career(topGamer.getCareer())
                .birthDate(topGamer.getBirthDate())
                .build();

        MemberChartDataDto chartDataDto = MemberChartDataDto.builder()
                .howManyMembers(club.getClubHowManyMembers())
                .howManyMembersBetweenOneMonth(membershipRepository.countMembershipsJoinedBetween(clubId,startDate,endDate))
                .howManyAccumulatedGuests(club.getClubAccumulatedGuests())
                .howManyGuestsBetweenOneMonth(guestUserIds.size())
                .howManyEventsBetweenOneMonth(clubEventRepository.countEventsBetween(clubId,startDate,endDate))
                .attendanceCount(attendanceCounts.size())
                .maleMembers(membershipRepository.countMembershipsWithGender(clubId, UserEntity.Gender.MALE))
                .femaleMembers(membershipRepository.countMembershipsWithGender(clubId, UserEntity.Gender.FEMALE))
                .mostAttendantMember(topAttendantUserInfo)
                .mostManyGamesMember(topGamerUserInfo)
                .build();

        System.out.println(chartDataDto.toString());
        return ResponseEntity.ok(chartDataDto);
    }

    public ResponseEntity<?> getAIMemberReport(Long clubId, int year, int month) {
        Optional<ClubEntity> clubOpt = clubRepository.findById(clubId);
        if (clubOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("clubId에 해당하는 club이 없습니다.");
        }
        ClubEntity club = clubOpt.get();


        LocalDate firstDay = LocalDate.of(year, month, 1);
        LocalDate lastDay = firstDay.withDayOfMonth(firstDay.lengthOfMonth());

        LocalDateTime startDate = firstDay.atStartOfDay();
        LocalDateTime endDate = lastDay.atTime(23, 59, 59);

        List<UserEntity> guestUserIds = guestRepository.findAttendedGuestUserIds(startDate, endDate);
        List<MembersReportDto> attendanceCounts = participantRepository.findMembershipAttendanceCount(startDate, endDate);
        UserEntity topAttendant = participantRepository.findTopEventParticipant(PageRequest.of(0, 1)).get(0);
        UserEntity topGamer = gameParticipantRepository.findTopGameParticipant(PageRequest.of(0, 1)).get(0);
        UserInfoDto topAttendantUserInfo = UserInfoDto.builder()
                .userName(topAttendant.getUserName())
                .userTel(topAttendant.getUserTel())
                .gender(topAttendant.getGender())
                .region(topAttendant.getRegion())
                .career(topAttendant.getCareer())
                .birthDate(topAttendant.getBirthDate())
                .build();

        UserInfoDto topGamerUserInfo = UserInfoDto.builder()
                .userName(topGamer.getUserName())
                .userTel(topGamer.getUserTel())
                .gender(topGamer.getGender())
                .region(topGamer.getRegion())
                .career(topGamer.getCareer())
                .birthDate(topGamer.getBirthDate())
                .build();

        MemberChartDataDto chartDataDto = MemberChartDataDto.builder()
                .year(year)
                .month(month)
                .howManyMembers(club.getClubHowManyMembers())
                .howManyMembersBetweenOneMonth(membershipRepository.countMembershipsJoinedBetween(clubId,startDate,endDate))
                .howManyAccumulatedGuests(club.getClubAccumulatedGuests())
                .howManyGuestsBetweenOneMonth(guestUserIds.size())
                .howManyEventsBetweenOneMonth(clubEventRepository.countEventsBetween(clubId,startDate,endDate))
                .attendanceCount(attendanceCounts.size())
                .maleMembers(membershipRepository.countMembershipsWithGender(clubId, UserEntity.Gender.MALE))
                .femaleMembers(membershipRepository.countMembershipsWithGender(clubId, UserEntity.Gender.FEMALE))
                .mostAttendantMember(topAttendantUserInfo)
                .mostManyGamesMember(topGamerUserInfo)
                .build();

        return openAIService.writeMemberReportWithAI(clubId, chartDataDto);
    }
}
