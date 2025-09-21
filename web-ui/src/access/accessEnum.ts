export const ACCESS_ENUM = {
  NO_LOGIN: 'noLogin',
  USER: 'user',
  ADMIN: 'admin',
}

// 定义权限等级映射表
export const ACCESS_LEVEL_MAP = {
  [ACCESS_ENUM.NO_LOGIN]: 0,
  [ACCESS_ENUM.USER]: 1,
  [ACCESS_ENUM.ADMIN]: 2,
};
