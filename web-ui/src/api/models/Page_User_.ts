/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
import type { OrderItem } from '@/api';
import type { User } from '@/api';
export type Page_User_ = {
    countId?: string;
    current?: number;
    maxLimit?: number;
    optimizeCountSql?: boolean;
    orders?: Array<OrderItem>;
    pages?: number;
    records?: Array<User>;
    searchCount?: boolean;
    size?: number;
    total?: number;
};

