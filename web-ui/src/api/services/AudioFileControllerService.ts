/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_string_, CancelablePromise } from '@/api'
import { OpenAPI } from '@/api'
import { request as __request } from '../core/request'
export class AudioFileControllerService {
  /**
   * upload
   * @param file file
   * @returns BaseResponse_string_ OK
   * @returns any Created
   * @throws ApiError
   */
  public static uploadUsingPost(file: Blob): CancelablePromise<BaseResponse_string_ | any> {
    return __request(OpenAPI, {
      method: 'POST',
      url: '/core/audio/upload',
      formData: {file},
      mediaType: 'multipart/form-data',
      errors: {
        401: `Unauthorized`,
        403: `Forbidden`,
        404: `Not Found`,
      },
    })
  }
}
