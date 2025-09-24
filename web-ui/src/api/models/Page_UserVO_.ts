/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
import type { OrderItem } from '@/api';
import type { UserVO } from '@/api';
export type Page_UserVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: Array<OrderItem>;
    pages?: number;
    records?: Array<UserVO>;
    searchCount?: boolean;
    size?: number;
    total?: number;
};

