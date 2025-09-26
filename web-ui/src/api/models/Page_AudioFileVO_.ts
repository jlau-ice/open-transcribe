/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
import type { AudioFileVO } from '@/api';
import type { OrderItem } from '@/api';
export type Page_AudioFileVO_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: Array<OrderItem>;
    pages?: number;
    records?: Array<AudioFileVO>;
    searchCount?: boolean;
    size?: number;
    total?: number;
};

