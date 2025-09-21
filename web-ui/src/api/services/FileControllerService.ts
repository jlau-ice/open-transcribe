/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { BaseResponse_string_,CancelablePromise} from '@/api';
import { OpenAPI } from '@/api';
import { request as __request } from '../core/request';
export class FileControllerService {
    /**
     * uploadFile
     * @param file file
     * @param biz
     * @returns BaseResponse_string_ OK
     * @returns any Created
     * @throws ApiError
     */
    public static uploadFileUsingPost(
        file: Blob,
        biz?: string,
    ): CancelablePromise<BaseResponse_string_ | any> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/file/upload',
            query: {
                'biz': biz,
            },
            formData: {
                'file': file,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
