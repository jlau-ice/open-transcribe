/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
import type { BaseResponse_List_TranscribeResultVO_, CancelablePromise} from '@/api';
import { OpenAPI } from '@/api';
import { request as __request } from '../core/request';
export class TranscribeResultControllerService {
    /**
     * list
     * @param audioId audioId
     * @returns BaseResponse_List_TranscribeResultVO_ OK
     * @throws ApiError
     */
    public static listUsingGet(
        audioId: number,
    ): CancelablePromise<BaseResponse_List_TranscribeResultVO_> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/core/result/list/{audioId}',
            path: {
                'audioId': audioId,
            },
            errors: {
                401: `Unauthorized`,
                403: `Forbidden`,
                404: `Not Found`,
            },
        });
    }
}
