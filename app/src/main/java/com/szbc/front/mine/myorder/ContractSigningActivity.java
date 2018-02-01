package com.szbc.front.mine.myorder;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szbc.android.R;
import com.szbc.base.BaseActivity;
import com.szbc.base.Config;
import com.szbc.dialog.MyCallback;
import com.szbc.dialog.MyDialog;
import com.szbc.tool.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 签署协议
 */
public class ContractSigningActivity extends BaseActivity {

    @BindView(R.id.view_need_offset)
    LinearLayout viewNeedOffset;
    @BindView(R.id.iv_navigation_bg)
    ImageView iv_navigation_bg;
    @BindView(R.id.tv_contract_text)
    TextView tvContractText;
    @BindView(R.id.layout_contract_confirm)
    LinearLayout layoutContractConfirm;
    @BindView(R.id.textView1)
    TextView textView1;
    private String orderNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_signing_activity);
        ButterKnife.bind(this);

        if (Build.VERSION.SDK_INT != 19 && Build.VERSION.SDK_INT != 20)
            StatusBarUtil.setTranslucentForImageView(this, 0, viewNeedOffset);
        if (Build.VERSION.SDK_INT < 20)
            iv_navigation_bg.getLayoutParams().height = getResources().getDimensionPixelOffset(R.dimen.dimen_45);

        context = this;
        initData();
        initView();
        if (getIntent() != null && getIntent().hasExtra("state12Gone")) {
            layoutContractConfirm.setBackgroundResource(R.drawable.button_normal_caculator_1);
            textView1.setText("已签署");
            layoutContractConfirm.setClickable(false);
        }
    }

    private void initView() {
        tvContractText.setText("代驾服务协议                                                                                         客户方：                                      （以下简称甲方） 代驾方：                 （以下简称乙方）                                                                     根据《中华人民共和国合同法》相关规定，甲乙双方在平等、自愿、公平、诚信的基础 上，就汽车代驾服务有关事宜，达成协议如下： 第一条  代驾服务                                          （一）甲方自愿选择乙方的汽车代驾服务项目，甲方在电话预约后，等候乙方指派代驾 司机到达甲方指定出发地点，由乙方代驾司机为其执行代驾服务。                                           （二）甲方车辆符合法律规定的上路许可（车辆行驶证、牌号等与车辆有关的手续齐全）， 并在保险期内，乙方具有合法的经营资格及具备合格的代驾人员及手续。                                     （三）乙方依法保障甲方的正当行程自由及秘密（在法律允许的范围内）。乙方有权拒绝 甲方非正当的行程安排及事项。 第二条  费用标准费用交纳                                                （一）市区代驾：市区范围（市城中区、南岸城区、江北城区）代驾起始点至目的地，在十五公里内，收费标准详见《附表一》；超出二十公里仍在市区内的按2元/公里加收；超 出市区范围根据服务时间及路线另议。                                                                   （三）乙方有权根据市场行情及相关监管部门政策调整代驾服务价格，并及时通知甲方。                     （四）甲方成为会员后，在有效期内享受协议签订时服务价格；超出时随调整后的价格执行。                                                                      （五）代驾服务完成后，代驾司机填写受理单（包括起始点、目的地、行驶公里数、服务时间、收费数额等），甲方签字确认。准会员以现金形式支付给代驾司机或乙方公司人员； 会员从其会员卡储值中扣减。                                                                      （六）甲方在确定由乙方进行代驾时一次性付清代驾费用 第三条  客户资料                              （一）甲方应保证成为会员的登记资料真实有效、准确、完整，并有义务配                               合乙方对登记有资料进行核实。                                                                     （二）乙方对甲方提供客户资料依法负有保密义务，但为建立与客户沟通渠道改善服务 工作，乙方可以使用本协议涉及的客户资料。 第四条  风险控制                                            （一）甲方提供代驾车辆符合安全行驶条件，不能带病带伤上路，更不得隐瞒车辆的不 安全状况，必须在保险期内。                                                                     （二）乙方提供的代驾司机必须身体健康，有丰富的行车经验，驾驶证在审核有效期内。                     （三）如因甲方车辆原因（并非甲方故意隐瞒、譬如有如召回案例的车辆）造成交通意外，造成的损失由保险公司处理；如因甲方车辆原因，甲方故意隐瞒的，甲方自行处理；如因乙方代驾司机技术原因造成的意外和损失，由乙方负责（在乙方的代驾风险基金中支付）。如因第三方因素造成的意外，由交通管理部门裁决后，由责任方承担，乙方协助处理；如因 不可抗力（如地震、台风、战争等）双方免责免赔。 第五条  会员权利及义务                                （一）成为会员会更快、更省的享受到乙方的服务，可享受乙方在外地城市加盟连锁店的同等级别会员服务，更可和乙方合作，成为代驾司机及优先得到乙方汽车广告机会及报酬。                     （二）此会员卡记名，会员尊享，不可多车，多人使用。                                               （三）此卡已经办理，要不退还，如需改动客户资料，请与乙方客服联系。 （四）乙方 根据甲方使用情况，以电子邮件的形式发送用详单，以便甲方核实。                                         （五）会员车上风档必须放置会员标识，以便乙方代驾司机寻找车辆。 （六）会员卡储值为十二个月有效期，不计息，期内执行会员价格，超出期限，执行乙方新的服务价格标准。                     （七）如会员同意，乙方免费给会员手机安装gps定位系统，以便更好服务。 （八）会 员储值不足一次市区代驾服务是最低收费额数时，需及时充值，以便继续享受会员待遇。                       （九）甲方在签订代驾服务协议后，应将车内贵重物件转移，避免丢失引发纠纷，如因甲方原因丢失车内物件的，乙方不负任何责任。甲方也可与乙方补充签订车内贵重物件委托\n" +
                "\n" +
                "保管的协议，直到甲方签字中止委托保管协议为止。 第六条  未尽事宜，双方协商解决。                      附表一：                                                                      市区代驾收费标准                                                                                 附表二：                                                                      长途代驾收费标准篇二：代驾合同                                                                   代驾合同                                                                     根据《中华人民共和国劳动合同法》以及相关法律、法规的规定，经甲乙双方协商一致， 乙方为甲方提供车辆代驾服务，为明确双方的权利和义务，特制定代驾服务内容和标准。                       一、服务内容                                                                      1、乙方须按甲方需求提供相应的代驾服务                                                            2、乙方提供的司机应当符合甲方所规定的司机基本标准                                                二、费用支付                                                                     1、甲方向乙方支付代驾服务费用，车辆支出费用（由乙方代驾司机非工作期间故意或过 失造成事故损失的除外）由甲方负责承担。                                                               2、用车期间，甲方不为乙方司机提供食宿，乙方司机的基本工资、各项社会保险等费用由甲方转入乙方帐户，乙方负责支付与代驾司机。 3、乙方司机每月工资、各项社会保险费甲方如不能按期支付与乙方，以及乙方未能按期支付与代驾司机的，违约方应自逾期之日起 每日按未支付总额5‰的比例向对方支付违约金。                                                          三、甲方的权利、义务与责任                                                                     1、乙方需按甲方的出车地点、时间要求等及时高效的提供司机及代驾服务。                              2、乙方应委派驾驶技术良好的司机为甲方驾驶所租赁的车辆,该司机需经甲方认可。                       3、乙方司机在代驾服务过程中有以下情形之一的，甲方可立即通知并退回乙方：                          （1）在试用期内不符合甲方工作要求的；                                                            （2）严重违反甲方劳动纪律、规章制度的；                                                          （3）严重工作失职，营私舞弊，给甲方造成重大经济损失的；                                          （4）被依法追究刑事责任的。                                                                     4、甲方要求乙方司机进入企业前需身体健康，并根据甲方的要求提供健康证明，体检不 合格的人员退回乙方，乙方自行安排；                                                                   5、乙方需制定并完善代驾司机管理规章制度，建立安全教育、安全形势分析、在职培训 等制度，明确服务内容、标准和工作流程，加强代驾司机的管理。                                           6、甲方应当在用车日前五日向乙方提供代驾服务需求具体内容，以便乙方安排调度。                      7、甲方应当按时、足额支付给乙方服务费用。                                                        四、乙方的权利、义务与责任                                                                     1、绝对禁止代驾司机饮酒后执行代驾送车任务。乙方有责任建立不定期抽查制度，甲方有义务协助监督。                                                                     2、乙方代驾司机的主要工作职责除正常驾驶车辆外，还应保证代驾期间甲方人员及车辆的安全。                                                                      3、乙方代驾司机在工作时应准时、有礼貌、服务热情、听从甲方负责人的合理调度。                      4、乙方司机代驾服务期间对知晓的信息涉及甲方企业及商业秘密或个人隐私的内容有保密的义务。                                                                      5、乙方代驾司机发生工作事故的，乙方接到甲方通知后，按相关保险条例妥善处理，并 负责办理申报和理赔事宜；                                                                             6、非甲方原因造成的交通事故及其它意外或人为事故所造成的损失最终由乙方负责承担。                    7、代驾司机应遵守甲乙双方的规章制度，服从甲、乙双方的工作安排与管理，因个人原");
    }

    private void initData() {
        if (getIntent().hasExtra("orderNum"))
            orderNum = getIntent().getStringExtra("orderNum");
        if (getIntent().hasExtra("isFromHistoryOrder")){
//            layoutContractConfirm.setVisibility(GONE);
            layoutContractConfirm.setBackgroundResource(R.drawable.button_normal_caculator_1);
            textView1.setText("已签署");
            layoutContractConfirm.setClickable(false);
        }
    }

    @OnClick({R.id.title_back, R.id.layout_contract_confirm})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                finish();
                break;
            case R.id.layout_contract_confirm:
                requestContractConfirm();
                break;
            default:
                break;
        }
    }

    private void requestContractConfirm() {
        String path = Config.httpIp + Config.Urls.saveOrderOperationLog;
        params = new RequestParams(path);
        params.addParameter("orderNum", orderNum);
        params.setConnectTimeout(10 * 1000);
        showLog(params.toString());
        mDialog = new MyDialog(this, "加载中...");
        mDialog.setDuration(300);
        mDialog.setMyCallback(new MyCallback() {
            @Override
            public void callback() {
            }

            @Override
            public void doing() {
                x.http().post(params, new Callback.CacheCallback<String>() {
                    @Override
                    public void onSuccess(String arg0) {
                        try {
                            JSONObject object = new JSONObject(arg0);
                            String result = object.getString("code");
                            String msg = object.getString("message");
                            if (result.equals("1")) {
                                showToast(msg, 1200);
                                setResult(MyOrderActivity.RESPONSE_CODE_CONTRACT);
                                finish();
                            } else {
                                showToast(msg, 1000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showToast("服务器连接失败", 1000);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                        if (mDialog != null) {
                            mDialog.hideDialog();
                            d = null;
                        }
                    }

                    @Override
                    public boolean onCache(String result) {
                        return false;
                    }
                });
            }
        });
        mDialog.showDialog();
    }
}
