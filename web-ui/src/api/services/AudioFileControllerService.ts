/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AudioFileQueryRequest, AudioFileUpdateRequest, BaseResponse_AudioFileVO_, BaseResponse_List_AudioFileVO_, BaseResponse_Page_AudioFileVO_, BaseResponse_string_, CancelablePromise } from '@/api'
import { OpenAPI } from '@/api'
import { request as __request } from '../core/request'

export class AudioFileControllerService {
  /**
   * deleteAudioFile
   * @param id id
   * @returns BaseResponse_string_ OK
   * @throws ApiError
   */
  public static deleteAudioFileUsingDelete(id: number): CancelablePromise<BaseResponse_string_> {
    return __request(OpenAPI, {
      method: 'DELETE',
      url: '/core/audio/delete/{id}',
      path: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
      },
    })
  }

  /**
   * getAudioFileVOById
   * @param id id
   * @returns BaseResponse_AudioFileVO_ OK
   * @throws ApiError
   */
  public static getAudioFileVoByIdUsingGet(id: number): CancelablePromise<BaseResponse_AudioFileVO_> {
    return __request(OpenAPI, {
      method: 'GET',
      url: '/core/audio/get/vo/{id}',
      path: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }

  /**
   * listAudioFileByPage
   * @param request request
   * @returns BaseResponse_Page_AudioFileVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listAudioFileByPageUsingPost(request: AudioFileQueryRequest): CancelablePromise<BaseResponse_Page_AudioFileVO_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/list/page/vo',
      body: request,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }

  /**
   * listAudioFile
   * @param request request
   * @returns BaseResponse_List_AudioFileVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static listAudioFileUsingPost(request: AudioFileQueryRequest): CancelablePromise<BaseResponse_List_AudioFileVO_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/list/vo',
      body: request,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }

  /**
   * upload
   * @param file file
   * @returns BaseResponse_AudioFileVO_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadUsingPost(file: Blob): CancelablePromise<BaseResponse_AudioFileVO_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/upload',
      formData: {
        file: file,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }

  /**
   * transcribe
   * @param id id
   * @returns BaseResponse_string_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static transcribeUsingPost(id: number): CancelablePromise<BaseResponse_string_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/start/transcribe/{id}',
      path: {
        id: id,
      },
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }
  /**
   * updateAudioFile
   * @param updateRequest updateRequest
   * @returns BaseResponse_string_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static updateAudioFileUsingPost(updateRequest: AudioFileUpdateRequest): CancelablePromise<BaseResponse_string_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/update',
      body: updateRequest,
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }
}
