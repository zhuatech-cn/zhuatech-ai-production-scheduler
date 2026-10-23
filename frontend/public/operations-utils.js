/* 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/
 * 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export const decisionLabels={PASS:'条件满足',REVIEW:'需复核',HOLD:'暂不满足'};
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function compareDecimal(a,b){const split=v=>{if(!/^[0-9]+(?:\.[0-9]+)?$/.test(String(v)))throw new Error('数字格式错误');const [i,f='']=String(v).split('.');return [i.replace(/^0+(?=\d)/,''),f.replace(/0+$/,'')];};const [ai,af]=split(a),[bi,bf]=split(b);if(ai.length!==bi.length)return ai.length>bi.length?1:-1;if(ai!==bi)return ai>bi?1:-1;const size=Math.max(af.length,bf.length),ax=af.padEnd(size,'0'),bx=bf.padEnd(size,'0');return ax===bx?0:ax>bx?1:-1;}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function validateField(field,value){if(typeof value!=='string'||!value.trim()||value.length>200)throw new Error(field.label+'：请填写200字以内内容');const v=value.trim();if(field.type==='ID'&&!/^[A-Za-z0-9][A-Za-z0-9_.:-]{0,79}$/.test(v))throw new Error(field.label+'：编号格式不正确');if(field.type==='NUMBER'){if(!/^[0-9]+(?:\.[0-9]+)?$/.test(v)||v.length>32)throw new Error(field.label+'：请填写非负十进制数字，不接受科学计数法');const fraction=(v.split('.')[1]||'').replace(/0+$/,'');if(fraction.length>field.scale||compareDecimal(v,field.min)<0||compareDecimal(v,field.max)>0)throw new Error(field.label+'：超出范围或小数位限制');}if(field.type==='ENUM'&&!field.options.includes(v))throw new Error(field.label+'：请选择有效选项');return v;}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function buildPayload(fields,rows,asOf){if(!Array.isArray(rows)||rows.length<1||rows.length>100)throw new Error('页面需要1至100条记录');if(!/^\d{4}-\d{2}-\d{2}$/.test(asOf))throw new Error('请选择核对日期');const ids=new Set();const records=rows.map((row,index)=>{const out={};try{for(const field of fields)out[field.key]=validateField(field,String(row[field.key]??''));const id=out.id.toLowerCase();if(ids.has(id))throw new Error('编号重复，不区分大小写');ids.add(id);}catch(e){throw new Error('第'+(index+1)+'条：'+e.message);}return out;});return {asOf,records};}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function csvEncode(rows){return '\uFEFF'+rows.map(row=>row.map(cell=>{let s=String(cell??'');if(/^[=+@\-]/.test(s.trimStart())||/^[\t\r]/.test(s))s="'"+s;return '"'+s.replaceAll('"','""')+'"';}).join(',')).join('\r\n')+'\r\n';}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function paginate(rows,page,size=50){const pages=Math.max(1,Math.ceil(rows.length/size)),current=Math.max(1,Math.min(pages,page));return {rows:rows.slice((current-1)*size,current*size),page:current,pages};}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function localDate(d=new Date()){return [d.getFullYear(),String(d.getMonth()+1).padStart(2,'0'),String(d.getDate()).padStart(2,'0')].join('-');}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function basicHeader(name,password){if(!name||!password||name.includes(':')||name.length>200||password.length>1024)throw new Error('请输入有效账号密码，账号不可含冒号');const bytes=new TextEncoder().encode(name+':'+password);return 'Basic '+btoa(String.fromCharCode(...bytes));}
/** 上海如静知华信息科技有限公司 · https://www.zhuatech.cn/ 商业授权或定制开发请微信添加微信号zhuatech或zhuatech2进行咨询。 */
export function display(value){return value===true?'是':value===false?'否':value==null?'—':String(value);}
