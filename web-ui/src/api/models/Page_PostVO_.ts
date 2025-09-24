/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
import type { OrderItem } from '@/api';
import type { PostVO } from '@/api';
export type Page_PostVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: Array<OrderItem>;
    pages?: number;
    records?: Array<PostVO>;
    searchCount?: boolean;
    size?: number;
    total?: number;
};

