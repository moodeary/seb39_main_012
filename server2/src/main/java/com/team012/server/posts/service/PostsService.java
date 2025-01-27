package com.team012.server.posts.service;

import com.team012.server.common.aws.service.AwsS3Service;
import com.team012.server.posts.dto.PostsCreateDto;
import com.team012.server.posts.dto.PostsUpdateDto;
import com.team012.server.posts.entity.Posts;
import com.team012.server.posts.img.dto.ImgUpdateDto;
import com.team012.server.posts.img.entity.PostsImg;
import com.team012.server.posts.img.service.PostsImgService;
import com.team012.server.posts.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class PostsService {

    private final PostsRepository postsRepository;
    private final PostsImgService postsImgService;
    private final AwsS3Service awsS3Service;

    @Transactional(propagation = Propagation.REQUIRED)
    public Posts save(PostsCreateDto post, List<MultipartFile> files, Long companyId) {

        LocalTime checkIn = convertCheckInToTime(post.getCheckInTime());
        LocalTime checkOut = convertCheckOutToTime(post.getCheckOutTime());
        validateCheckInCheckOut(checkIn, checkOut);

        Posts posts = Posts.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .companyId(companyId)
                .latitude(post.getLatitude())
                .longitude(post.getLongitude())
                .address(post.getAddress())
                .detailAddress(post.getDetailAddress())
                .phone(post.getPhone())
                .roomCount(post.getRoomCount())//add
                .checkInTime(checkIn)
                .checkOutTime(checkOut)
                .build();

        List<PostsImg> lists = awsS3Service.convertPostImg(files);

        posts.setPostsImgList(lists);
        for (PostsImg c : lists) {
            c.setPosts(posts);
        }

        return postsRepository.save(posts);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Posts update(PostsUpdateDto post, List<MultipartFile> files, Long companyId) throws FileUploadException {
        Long postsId = post.getId();
        Posts findPosts = findById(postsId);
        log.info(findPosts.getTitle() ,"{}");

        if (!Objects.equals(findPosts.getCompanyId(), companyId)) throw new RuntimeException("companyId 일치하지 않음");

        Optional.ofNullable(post.getLatitude()).ifPresent(findPosts::setLatitude);
        Optional.ofNullable(post.getLongitude()).ifPresent(findPosts::setLongitude);
        Optional.ofNullable(post.getAddress()).ifPresent(findPosts::setAddress);
        Optional.ofNullable(post.getDetailAddress()).ifPresent(findPosts::setDetailAddress);
        Optional.ofNullable(post.getPhone()).ifPresent(findPosts::setPhone);
        Optional.ofNullable(post.getTitle()).ifPresent(findPosts::setTitle);
        Optional.ofNullable(post.getContent()).ifPresent(findPosts::setContent);
        Optional.ofNullable(post.getRoomCount()).ifPresent(findPosts::setRoomCount); //add

        if (StringUtils.hasText(post.getCheckInTime())) {
            LocalTime checkIn = convertCheckInToTime(post.getCheckInTime());
            findPosts.setCheckInTime(checkIn);
        }
        if (StringUtils.hasText(post.getCheckOutTime())) {
            LocalTime checkOut = convertCheckOutToTime(post.getCheckOutTime());
            findPosts.setCheckOutTime(checkOut);
        }
        validateCheckInCheckOut(findPosts.getCheckInTime(), findPosts.getCheckOutTime());

        if (!CollectionUtils.isEmpty(files)) {
            List<PostsImg> postsImgList = postsImgService.updatePostsImg(files, postsId);
            findPosts.setPostsImgList(postsImgList);
            for (PostsImg postsImg : postsImgList) {
                postsImg.setPosts(findPosts);
            }
        }

        Posts posts1 = postsRepository.save(findPosts);
        return posts1;

    }

    @Transactional(readOnly = true)
    public Posts findById(Long postsId) {
        Optional<Posts> findCompanyPosts
                = postsRepository.findById(postsId);

        return findCompanyPosts.orElseThrow(() -> new RuntimeException("Posts Not Found"));
    }

    public void delete(Long postsId, Long companyId) {
        Posts posts = findById(postsId);
        if (posts.getCompanyId() != companyId) throw new RuntimeException("companyId가 일치하지 않음");
        awsS3Service.deleteFile(posts.getPostsImgList());
        postsRepository.delete(posts);
    }

    public void savedLikesCount(Posts posts) {
        postsRepository.save(posts);
    }

    public void save(Posts posts) {
        postsRepository.save(posts);
    }


    public Posts findByCompanyId(Long companyId) {
        return postsRepository.findByCompanyId(companyId);
    }

    private LocalTime convertCheckInToTime(String strCheckIn) {
        strCheckIn = strCheckIn.trim();

        return LocalTime.parse(strCheckIn, DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
    }

    private LocalTime convertCheckOutToTime(String strCheckOut) {
        strCheckOut = strCheckOut.trim();

        return LocalTime.parse(strCheckOut, DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
    }

    private void validateCheckInCheckOut(LocalTime checkIn, LocalTime checkOut) {
        if(checkOut.isBefore(checkIn)) throw new IllegalArgumentException("checkIn must be lesser then checkOut");
    }
}
