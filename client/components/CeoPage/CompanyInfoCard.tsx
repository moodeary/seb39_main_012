import React from 'react'
import styled from 'styled-components'
import CardImage from '../CardImage/CardImage'
import CompanyInfoTag from './CompanyInfoTag'

const imgUrl = 'https://cdn.imweb.me/upload/S201807025b39d1981b0b0/1fc55d055098b.jpg'

function CompanyInfoCard() {
  return (
    <Contaienr>
      <CardImage mode="post" imgUrl={imgUrl}></CardImage>
      <Title>JW에리어트 멍멍스퀘어 서울</Title>
      <Content>
        <Price>30,000원 / 1박</Price>
        <CompanyInfoTag title={'수정'} onClick={() => console.log('수정')} />
        <CompanyInfoTag title={'삭제'} onClick={() => console.log('수정')} />
      </Content>
    </Contaienr>
  )
}

export default CompanyInfoCard

const Contaienr = styled.div`
  display: flex;
  flex-direction: column;
  gap: 5px;
  width: 250px;

  @media (max-width: 768px) {
    width: 200px;
  }

  @media (max-width: 390px) {
    width: 170px;
  }
`

const Title = styled.h1`
  font-family: 'Inter';
  font-style: normal;
  font-weight: 600;
  font-size: 15px;
  line-height: 18px;
  display: flex;

  @media (max-width: 768px) {
    font-size: 12px;
  }
`
const Content = styled.div`
  display: flex;
  align-items: center;
  gap: 5px;
`

const Price = styled.p`
  font-family: 'Inter';
  font-style: normal;
  font-weight: 800;
  font-size: 15px;
  line-height: 18px;
`
