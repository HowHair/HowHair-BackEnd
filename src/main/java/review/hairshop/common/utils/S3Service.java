package review.hairshop.common.utils;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import review.hairshop.common.response.ApiException;
import review.hairshop.common.response.ApiResponseStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    /** *
     * 1. [인자로 넘어온 image를 , 인자로 넘어온 path에 저장하는 서비스]
     */
    public void uploadFileList(List<String> pathList, List<MultipartFile> imageList){

        try {
            //1. meta 데이터를 담을 리스트 생성 후 데이터 넣기
            List<ObjectMetadata> metadataList = new ArrayList<>(imageList.size());

            for(int i=0; i<imageList.size(); i++){
                metadataList.add(new ObjectMetadata());
                metadataList.get(i).setContentType(imageList.get(i).getContentType());
                metadataList.get(i).setContentLength(imageList.get(i).getSize());
            }

            //2. 이제 실제 그 path에 , 실제 이미지와 , 메타데이터를 함께 저장
            for(int i=0; i<imageList.size(); i++){
                amazonS3Client.putObject(bucket, pathList.get(i), imageList.get(i).getInputStream(), metadataList.get(i));
            }

        } catch (IOException e) {
            throw new ApiException(ApiResponseStatus.FAIL_SAVE_IMAGE, "알 수 없는 이유로, 이미지를 s3 저장소에 저장하는데 실패하였습니다.");
        }
    }

}
