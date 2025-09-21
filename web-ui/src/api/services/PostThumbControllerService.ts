/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type {BaseResponse_int_, PostThumbAddRequest, CancelablePromise} from '@/api';
import {OpenAPI} from '@/api';
import {request as __request} from '../core/request';

export class PostThumbControllerService {
    /**
     * doThumb
     * @param postThumbAddRequest postThumbAddRequest
     * @returns BaseResponse_int_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static doThumbUsingPost(
        postThumbAddRequest: PostThumbAddRequest,
    ): CancelablePromise<BaseResponse_int_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/post_thumb/',
            body: postThumbAddRequest,
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
