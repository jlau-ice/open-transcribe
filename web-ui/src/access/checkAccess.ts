import { ACCESS_ENUM, ACCESS_LEVEL_MAP } from './accessEnum'
import { type LoginUserVO } from '@/api'
/**
 * 检查用户权限
 * @param loginUser 登录用户对象
 * @param needAccess 所需权限字符串
 */
const checkAccess = (loginUser: LoginUserVO, needAccess: string) => {
  // 1. 如果不需要任何权限，则直接通过
  if (!needAccess || needAccess.length === 0) {
    return true
  }
  // 2. 获取用户角色，如果没有则默认为 noLogin
  const loginUserRole = loginUser?.userRole ?? ACCESS_ENUM.NO_LOGIN
  // 3. 获取用户和所需权限的等级
  const userLevel = ACCESS_LEVEL_MAP[loginUserRole]
  const needLevel = ACCESS_LEVEL_MAP[needAccess]
  // 4. 如果用户等级大于或等于所需等级，则通过
  // 例如：userLevel (2) >= needLevel (1) -> true
  return userLevel >= needLevel
}
export default checkAccess
